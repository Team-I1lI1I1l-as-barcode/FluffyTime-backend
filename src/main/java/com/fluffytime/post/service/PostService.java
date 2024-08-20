package com.fluffytime.post.service;

import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.PostNotFound;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.PostImages;
import com.fluffytime.domain.TempStatus;
import com.fluffytime.domain.User;
import com.fluffytime.post.aws.S3Service;
import com.fluffytime.post.dto.request.PostRequest;
import com.fluffytime.post.dto.response.PostResponse;
import com.fluffytime.post.exception.ContentLengthExceeded;
import com.fluffytime.post.exception.FileSizeExceeded;
import com.fluffytime.post.exception.FileUploadFailed;
import com.fluffytime.post.exception.PostNotInTempStatus;
import com.fluffytime.post.exception.TooManyFiles;
import com.fluffytime.post.exception.UnsupportedFileFormat;
import com.fluffytime.repository.PostImagesRepository;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImagesRepository postImagesRepository;
    private final JwtTokenizer jwtTokenizer;
    private final S3Service s3Service;

    // 게시글 등록하기
    @Transactional
    public Long createPost(PostRequest postRequest, MultipartFile[] files,
        HttpServletRequest request) {
        // 업로드된 파일들의 유효성을 검증함
        validateFiles(files);

        User user = findUserByAccessToken(request);
        Post post;

        if (postRequest.getTempId() != null) {
            // 임시 저장된 글을 가져옴
            post = postRepository.findById(postRequest.getTempId())
                .orElseThrow(PostNotFound::new);

            // 상태 검증
            if (post.getTempStatus() != TempStatus.TEMP) {
                throw new PostNotInTempStatus();  // 상태가 올바르지 않으면 예외 발생
            }

            // 상태를 최종 등록으로 업데이트
            post.setTempStatus(TempStatus.SAVE);
            post.setUpdatedAt(LocalDateTime.now());
            post.setContent(postRequest.getContent());
        } else {
            // 새 게시물 생성
            post = Post.builder()
                .user(user)
                .content(postRequest.getContent())
                .createdAt(LocalDateTime.now())
                .tempStatus(TempStatus.SAVE)  // 새로 생성되는 게시물은 최종 등록 상태로 설정
                .build();
            postRepository.save(post);
        }

        // 이미지 저장 로직
        if (files != null && files.length > 0) {
            savePostImages(files, post);
        }

        return post.getPostId();  // 생성된 게시물의 ID를 반환
    }

    // 임시 게시글 등록하기
    @Transactional
    public Long createTempPost(PostRequest postRequest, MultipartFile[] files,
        HttpServletRequest request) {
        // 업로드된 파일들의 유효성을 검증함
        validateFiles(files);

        User user = findUserByAccessToken(request);

        // 임시 게시물을 생성함
        Post post = Post.builder()
            .user(user)
            .content(postRequest.getContent())
            .createdAt(LocalDateTime.now())
            .tempStatus(TempStatus.TEMP)
            .build();

        postRepository.save(post);

        if (files != null && files.length > 0) {
            savePostImages(files, post);
        }

        return post.getPostId(); // 생성된 임시 게시물의 ID를 반환
    }

    // 이미지 파일 저장 로직
    private void savePostImages(MultipartFile[] files, Post post) {
        for (MultipartFile file : files) {
            try {
                // 이미지를 S3에 업로드하고 URL을 가져옴
                String fileName = s3Service.uploadFile(file);
                String fileUrl = s3Service.getFileUrl(fileName);

                // PostImages 엔티티를 생성하여 데이터베이스에 저장함
                PostImages postImage = PostImages.builder()
                    .filename(fileName)
                    .filepath(fileUrl)
                    .filesize(file.getSize())
                    .mimetype(file.getContentType())
                    .post(post)
                    .build();

                postImagesRepository.save(postImage);
            } catch (Exception e) {
                throw new FileUploadFailed();
            }
        }
    }

    // 게시글 조회하기
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        // 게시글을 조회하고, 없으면 예외를 발생시킴
        Post post = postRepository.findById(id)
            .orElseThrow(PostNotFound::new);

        // Post 엔티티를 PostResponse로 변환함
        return new PostResponse(
            post.getPostId(),
            post.getContent(),
            post.getPostImages().stream().map(image -> new PostResponse.ImageResponse(
                image.getImageId(),
                image.getFilename(),
                image.getFilepath(),
                image.getFilesize(),
                image.getMimetype(),
                image.getDescription(),
                image.getUploadDate().format(DateTimeFormatter.ISO_DATE_TIME)
            )).collect(Collectors.toList()),
            post.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME),
            post.getUpdatedAt() != null ? post.getUpdatedAt()
                .format(DateTimeFormatter.ISO_DATE_TIME) : null
        );
    }

    // 게시글 수정하기
    @Transactional
    public PostResponse updatePost(Long id, PostRequest postRequest, MultipartFile[] files,
        HttpServletRequest request) {
        // 토큰을 통해 사용자 정보 추출
        User user = findUserByAccessToken(request);

        Post existingPost = postRepository.findById(id)
            .orElseThrow(PostNotFound::new);

        // 게시물 소유자인지 확인
        if (!existingPost.getUser().equals(user)) {
            throw new UserNotFound(); // 권한이 없으면 NotFoundUser 예외 발생
        }

        // 게시물 내용의 길이를 검증함
        if (postRequest.getContent() != null && postRequest.getContent().length() > 2200) {
            throw new ContentLengthExceeded();
        }

        // 게시물 내용을 업데이트함
        existingPost.setContent(postRequest.getContent());

        // 새로운 파일이 업로드된 경우 이미지를 저장함(일단은 안됨)
        if (files != null && files.length > 0) {
            savePostImages(files, existingPost);
        }

        existingPost.setUpdatedAt(LocalDateTime.now());
        postRepository.save(existingPost);

        // Post 엔티티를 PostResponse로 변환하여 반환
        return new PostResponse(
            existingPost.getPostId(),
            existingPost.getContent(),
            existingPost.getPostImages().stream().map(image -> new PostResponse.ImageResponse(
                image.getImageId(),
                image.getFilename(),
                image.getFilepath(),
                image.getFilesize(),
                image.getMimetype(),
                image.getDescription(),
                image.getUploadDate().format(DateTimeFormatter.ISO_DATE_TIME)
            )).collect(Collectors.toList()),
            existingPost.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME),
            existingPost.getUpdatedAt() != null ? existingPost.getUpdatedAt()
                .format(DateTimeFormatter.ISO_DATE_TIME) : null
        );
    }

    // 게시글 삭제하기
    @Transactional
    public void deletePost(Long id, HttpServletRequest request) {
        // 토큰을 통해 사용자 정보 추출
        User user = findUserByAccessToken(request);

        Post post = postRepository.findById(id)
            .orElseThrow(PostNotFound::new);

        // 게시물 소유자인지 확인
        if (!post.getUser().equals(user)) {
            throw new UserNotFound();
        }

        postRepository.deleteById(id);
    }

    // 임시 게시글 삭제하기
    @Transactional
    public void deleteTempPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(PostNotFound::new);

        // 임시 저장된 상태인 경우에만 삭제함
        if (post.getTempStatus() == TempStatus.TEMP) {
            postRepository.deleteById(id);
            log.info("게시물 ID {}가 성공적으로 삭제되었습니다.", id);
        } else {
            throw new PostNotInTempStatus();
        }
    }

    // 임시 게시글 목록 조회하기
    @Transactional(readOnly = true)
    public List<PostResponse> getTempPosts() {
        // 모든 게시글을 조회한 후, 임시 저장된 게시물만 필터링함
        List<Post> tempPosts = postRepository.findAll().stream()
            .filter(post -> post.getTempStatus() == TempStatus.TEMP)
            .collect(Collectors.toList());

        // Post 엔티티 리스트를 PostResponse 리스트로 변환하여 반환
        return tempPosts.stream().map(post -> new PostResponse(
            post.getPostId(),
            post.getContent(),
            post.getPostImages().stream().map(image -> new PostResponse.ImageResponse(
                image.getImageId(),
                image.getFilename(),
                image.getFilepath(),
                image.getFilesize(),
                image.getMimetype(),
                image.getDescription(),
                image.getUploadDate().format(DateTimeFormatter.ISO_DATE_TIME)
            )).collect(Collectors.toList()),
            post.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME),
            post.getUpdatedAt() != null ? post.getUpdatedAt()
                .format(DateTimeFormatter.ISO_DATE_TIME) : null
        )).collect(Collectors.toList());
    }

    // 파일 검증 로직
    private void validateFiles(MultipartFile[] files) {
        if (files == null) {
            return; // null이면 파일 검증을 할 필요가 없음
        }

        if (files.length > 10) {
            throw new TooManyFiles();
        }
        for (MultipartFile file : files) {
            if (file.getSize() > 10485760) { // 10MB 이상
                throw new FileSizeExceeded();
            }
            if (!isSupportedFormat(file.getContentType())) {
                throw new UnsupportedFileFormat();
            }
        }
    }

    // 지원되는 파일 형식인지 확인
    private boolean isSupportedFormat(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals(
            "image/png"));
    }

    // accessToken을 통해 사용자 정보 추출
    @Transactional(readOnly = true)
    public User findUserByAccessToken(HttpServletRequest httpServletRequest) {
        log.info("findUserByAccessToken 실행");

        String accessToken = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null || accessToken.isEmpty()) {
            log.error("토큰이 존재하지 않습니다.");
            throw new IllegalArgumentException("토큰이 존재하지 않습니다.");
        }

        log.info("Extracted Token: {}", accessToken);

        try {
            Long userId = jwtTokenizer.getUserIdFromToken(accessToken);
            return userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 토큰입니다: {}", accessToken, e);
            throw new UserNotFound();
        } catch (Exception e) {
            log.error("토큰 처리 중 오류가 발생했습니다: {}", accessToken, e);
            throw new UserNotFound();
        }
    }
}
