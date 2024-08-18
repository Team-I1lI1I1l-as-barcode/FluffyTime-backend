package com.fluffytime.post.service;

import com.fluffytime.common.exception.global.NotFoundPost;
import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.PostImages;
import com.fluffytime.domain.TempStatus;
import com.fluffytime.domain.User;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.post.aws.S3Service;
import com.fluffytime.post.dto.request.PostRequest;
import com.fluffytime.post.dto.response.ApiResponse;
import com.fluffytime.post.dto.response.PostResponse;
import com.fluffytime.post.dto.response.PostResponseCode;
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
    public ApiResponse<Long> createPost(PostRequest postRequest, MultipartFile[] files,
        HttpServletRequest request) {
        validateFiles(files);

        // 토큰에서 사용자 ID 추출
        User user = findUserByAccessToken(request);

        if (postRequest.getTempStatus() == null) {
            postRequest.setTempStatus(TempStatus.SAVE);
        }

        Post post = Post.builder()
            .user(user)
            .content(postRequest.getContent())
            .createdAt(LocalDateTime.now())
            .tempStatus(postRequest.getTempStatus())
            .build();

        postRepository.save(post);

        if (files != null && files.length > 0) {
            savePostImages(files, post);
        }

        return ApiResponse.response(PostResponseCode.CREATE_POST_SUCCESS, post.getPostId());
    }

    // 이미지 파일 저장 로직
    private void savePostImages(MultipartFile[] files, Post post) {
        for (MultipartFile file : files) {
            try {
                String fileName = s3Service.uploadFile(file);
                String fileUrl = s3Service.getFileUrl(fileName);

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
                .orElseThrow(NotFoundUser::new);

        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 토큰입니다: {}", accessToken, e);
            throw new NotFoundUser();
        } catch (Exception e) {
            log.error("토큰 처리 중 오류가 발생했습니다: {}", accessToken, e);
            throw new NotFoundUser();
        }
    }

    // 임시 게시글 등록하기
    @Transactional
    public ApiResponse<Long> createTempPost(PostRequest postRequest, MultipartFile[] files,
        HttpServletRequest request) {
        validateFiles(files);

        User user = findUserByAccessToken(request);

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

        return ApiResponse.response(PostResponseCode.TEMP_SAVE_POST_SUCCESS, post.getPostId());
    }

    // 게시글 조회하기
    @Transactional(readOnly = true)
    public ApiResponse<PostResponse> getPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(NotFoundPost::new);

        // PostResponse로 변환
        PostResponse postResponse = new PostResponse(
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

        return ApiResponse.response(PostResponseCode.GET_POST_SUCCESS, postResponse);
    }

    // 게시글 수정하기
    @Transactional
    public ApiResponse<PostResponse> updatePost(Long id, PostRequest postRequest,
        MultipartFile[] files, HttpServletRequest request) {
        // 토큰을 통해 사용자 정보 추출
        User user = findUserByAccessToken(request);

        Post existingPost = postRepository.findById(id)
            .orElseThrow(NotFoundPost::new);

        // 게시물 소유자인지 확인
        if (!existingPost.getUser().equals(user)) {
            throw new NotFoundUser(); // 권한이 없으면 NotFoundUser 예외 발생
        }

        if (postRequest.getContent() != null && postRequest.getContent().length() > 2200) {
            throw new ContentLengthExceeded();
        }
        existingPost.setContent(postRequest.getContent());

        if (files != null && files.length > 0) {
            savePostImages(files, existingPost);
        }

        existingPost.setUpdatedAt(LocalDateTime.now());
        postRepository.save(existingPost);

        PostResponse postResponse = new PostResponse(
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

        return ApiResponse.response(PostResponseCode.UPDATE_POST_SUCCESS, postResponse);
    }

    // 게시글 삭제하기
    @Transactional
    public ApiResponse<Void> deletePost(Long id, HttpServletRequest request) {
        // 토큰을 통해 사용자 정보 추출
        User user = findUserByAccessToken(request);

        Post post = postRepository.findById(id)
            .orElseThrow(NotFoundPost::new);

        // 게시물 소유자인지 확인
        if (!post.getUser().equals(user)) {
            throw new NotFoundUser(); // 권한이 없으면 NotFoundUser 예외 발생
        }

        postRepository.deleteById(id);
        return ApiResponse.response(PostResponseCode.DELETE_POST_SUCCESS);
    }

    // 임시 게시글 삭제하기
    @Transactional
    public ApiResponse<Void> deleteTempPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(NotFoundPost::new);

        if (post.getTempStatus() == TempStatus.TEMP) {
            postRepository.deleteById(id);
            log.info("게시물 ID {}가 성공적으로 삭제되었습니다.", id);
            return ApiResponse.response(PostResponseCode.DELETE_TEMP_POST_SUCCESS);
        } else {
            throw new PostNotInTempStatus();
        }
    }

    // 임시 게시글 목록 조회하기
    @Transactional(readOnly = true)
    public ApiResponse<List<PostResponse>> getTempPosts() {
        List<Post> tempPosts = postRepository.findAll().stream()
            .filter(post -> post.getTempStatus() == TempStatus.TEMP)
            .collect(Collectors.toList());

        List<PostResponse> tempPostResponses = tempPosts.stream().map(post -> new PostResponse(
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

        return ApiResponse.response(PostResponseCode.GET_TEMP_POSTS_SUCCESS, tempPostResponses);
    }

    // 파일 검증 로직
    private void validateFiles(MultipartFile[] files) {
        if (files == null) {
            return; // null이면 파일 검증을 할 필요가 없습니다.
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
}
