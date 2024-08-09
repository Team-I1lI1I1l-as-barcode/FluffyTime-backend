package com.fluffytime.post.service;

import com.fluffytime.common.exception.global.NotFoundPost;
import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.Post;
import com.fluffytime.domain.TempStatus;
import com.fluffytime.domain.User;
import com.fluffytime.login.security.CustomUserDetails;
import com.fluffytime.post.aws.S3Service;
import com.fluffytime.post.dto.request.PostRequest;
import com.fluffytime.post.dto.response.ApiResponse;
import com.fluffytime.post.dto.response.PostResponseCode;
import com.fluffytime.post.exception.ContentLengthExceeded;
import com.fluffytime.post.exception.FileSizeExceeded;
import com.fluffytime.post.exception.FileUploadFailed;
import com.fluffytime.post.exception.PostNotInTempStatus;
import com.fluffytime.post.exception.TooManyFiles;
import com.fluffytime.post.exception.UnsupportedFileFormat;
import com.fluffytime.repository.PostRepository;
import com.fluffytime.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 게시글 등록하기
    @Transactional
    public ApiResponse<Long> createPost(PostRequest postRequest, MultipartFile[] files) {
        validateFiles(files);

        // 현재 인증된 사용자 ID 가져오기
        Long userId = getCurrentUserId();

        // 유저 정보 가져오기
        User user = userRepository.findById(userId)
            .orElseThrow(NotFoundUser::new);

        if (postRequest.getTempStatus() == null) {
            postRequest.setTempStatus(TempStatus.SAVE);
        }

        List<String> imageUrls = uploadFiles(files).getData();

        if (postRequest.getContent().length() > 2200) {
            throw new ContentLengthExceeded();
        }

        Post post = Post.builder()
            .user(user)
            .content(postRequest.getContent())
            .createdAt(LocalDateTime.now())
            .tempStatus(postRequest.getTempStatus())
            .imageUrls(imageUrls)
            .build();

        postRepository.save(post);
        return ApiResponse.response(PostResponseCode.CREATE_POST_SUCCESS, post.getPostId());
    }

    // 임시 게시글 등록하기
    @Transactional
    public ApiResponse<Long> createTempPost(PostRequest postRequest, MultipartFile[] files) {
        validateFiles(files);

        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(NotFoundUser::new);

        List<String> imageUrls = uploadFiles(files).getData();

        Post post = Post.builder()
            .user(user)
            .content(postRequest.getContent())
            .createdAt(LocalDateTime.now())
            .tempStatus(TempStatus.TEMP)
            .imageUrls(imageUrls)
            .build();

        postRepository.save(post);
        return ApiResponse.response(PostResponseCode.TEMP_SAVE_POST_SUCCESS, post.getPostId());
    }

    // 파일 업로드 메서드 확장
    private ApiResponse<List<String>> uploadFiles(MultipartFile[] files) {
        List<String> imageUrls = List.of(files).stream().map(file -> {
            try {
                String fileName = s3Service.uploadFile(file);
                return s3Service.getFileUrl(fileName);
            } catch (Exception e) {
                throw new FileUploadFailed();
            }
        }).collect(Collectors.toList());

        return ApiResponse.response(PostResponseCode.UPLOAD_FILE_SUCCESS, imageUrls);
    }

    // 현재 인증된 사용자 ID 가져오기
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // CustomUserDetails에서 이메일을 통해 사용자 ID를 가져옴
            String email = userDetails.getUsername(); // 이메일 가져오기
            return userRepository.findByEmail(email)
                .orElseThrow(NotFoundUser::new)
                .getUserId();  // 사용자 ID 반환
        } else {
            throw new NotFoundUser();
        }
    }


    private void validateFiles(MultipartFile[] files) {
        if (files.length > 10) {
            throw new TooManyFiles();
        }
        for (MultipartFile file : files) {
            if (file.getSize() > 10485760) {
                throw new FileSizeExceeded();
            }
            if (!isSupportedFormat(file.getContentType())) {
                throw new UnsupportedFileFormat();
            }
        }
    }

    private boolean isSupportedFormat(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals(
            "image/png"));
    }

    // 게시글 조회하기
    @Transactional(readOnly = true)
    public ApiResponse<Post> getPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(NotFoundPost::new);
        return ApiResponse.response(PostResponseCode.GET_POST_SUCCESS, post);
    }

    // 게시글 수정하기
    @Transactional
    public ApiResponse<Post> updatePost(Long id, PostRequest postRequest, MultipartFile[] files) {
        Post existingPost = getPostById(id).getData();

        if (postRequest.getContent() != null && postRequest.getContent().length() > 2200) {
            throw new ContentLengthExceeded();
        }
        existingPost.setContent(postRequest.getContent());

        if (files != null && files.length > 0) {
            ApiResponse<List<String>> uploadResponse = uploadFiles(files);
            List<String> imageUrls = uploadResponse.getData();
            existingPost.setImageUrls(imageUrls);
        }

        existingPost.setUpdatedAt(LocalDateTime.now());
        postRepository.save(existingPost);
        return ApiResponse.response(PostResponseCode.UPDATE_POST_SUCCESS, existingPost);
    }

    // 게시글 삭제하기
    @Transactional
    public ApiResponse<Void> deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new NotFoundPost();
        }
        postRepository.deleteById(id);
        return ApiResponse.response(PostResponseCode.DELETE_POST_SUCCESS);
    }

    // 임시 게시글 삭제하기
    @Transactional
    public ApiResponse<Void> deleteTempPost(Long id) {
        Post post = getPostById(id).getData();
        if (post.getTempStatus() == TempStatus.TEMP) {
            postRepository.deleteById(id);
            return ApiResponse.response(PostResponseCode.DELETE_TEMP_POST_SUCCESS);
        } else {
            throw new PostNotInTempStatus();
        }
    }

    // 임시 게시글 목록 조회하기
    @Transactional(readOnly = true)
    public ApiResponse<List<Post>> getTempPosts() {
        List<Post> tempPosts = postRepository.findAll().stream()
            .filter(post -> post.getTempStatus() == TempStatus.TEMP)
            .collect(Collectors.toList());
        return ApiResponse.response(PostResponseCode.GET_TEMP_POSTS_SUCCESS, tempPosts);
    }
}
