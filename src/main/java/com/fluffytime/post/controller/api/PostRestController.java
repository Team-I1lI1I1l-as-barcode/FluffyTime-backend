package com.fluffytime.post.controller.api;

import com.fluffytime.domain.Post;
import com.fluffytime.domain.TempStatus;
import com.fluffytime.post.aws.S3Service;
import com.fluffytime.post.dto.PostRequest;
import com.fluffytime.post.dto.PostResponse;
import com.fluffytime.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;
    private final S3Service s3Service;

    // 게시물 등록 및 임시 저장된 글 삭제
    @PostMapping("/reg")
    public ResponseEntity<Long> regPost(@RequestPart("post") PostRequest postRequest,
        @RequestPart("images") MultipartFile[] files, HttpServletRequest request) {
        Long postId = handlePostRequest(postRequest, files, TempStatus.SAVE, request);

        if (postRequest.getTempId() != null) {
            // 임시 저장된 글 삭제
            postService.deleteTempPost(postRequest.getTempId());
        }

        return ResponseEntity.created(URI.create("/api/posts/" + postId)).body(postId);
    }

    // 게시물 임시등록
    @PostMapping("/temp-reg")
    public ResponseEntity<Long> tempRegPost(@RequestPart("post") PostRequest postRequest,
        @RequestPart("images") MultipartFile[] files, HttpServletRequest request) {
        Long postId = handlePostRequest(postRequest, files, TempStatus.TEMP, request);
        return ResponseEntity.created(URI.create("/api/posts/" + postId)).body(postId);
    }

    private Long handlePostRequest(PostRequest postRequest, MultipartFile[] files,
        TempStatus tempStatus, HttpServletRequest request) {
        try {
            postRequest.setTempStatus(tempStatus); // 임시 상태 설정
            log.info("Received post request: {}", postRequest);

            if (files.length > 10) {
                log.error("Too many files uploaded: {}", files.length);
                throw new IllegalArgumentException("Too many files uploaded");
            }
            // 기존에 업로드된 이미지 URL 리스트 초기화
            postRequest.setImageUrls(new ArrayList<>());

            for (MultipartFile file : files) {
                String fileName = s3Service.uploadFile(file);
                String fileUrl = s3Service.getFileUrl(fileName);
                postRequest.getImageUrls().add(fileUrl); // 파일 업로드 및 URL 추가
                log.info("Uploaded file: {}, URL: {}", fileName, fileUrl);
            }

            return postService.createPost(postRequest, new MultipartFile[]{},
                request); // 빈 배열로 전달하여 중복 업로드 방지
        } catch (Exception e) {
            log.error("Failed to upload files", e);
            throw new RuntimeException("Failed to upload files", e);
        }
    }

    // 임시 등록 게시물 삭제하기
    @PostMapping("/temp-delete/{id}")
    public ResponseEntity<Void> deleteTempPost(@PathVariable Long id) {
        try {
            postService.deleteTempPost(id);// 임시 게시물 삭제
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to delete temp post", e);
            return ResponseEntity.status(500).build();
        }
    }

    // 임시 등록 게시물 목록 불러오기
    @GetMapping("/temp-posts/list")
    public ResponseEntity<List<Post>> getTempPosts() {
        List<Post> tempPosts = postService.getTempPosts(); // 임시 게시물 목록 조회
        return ResponseEntity.ok(tempPosts);
    }

    // 게시물 삭제하기
    @GetMapping("/detail/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id); // 게시물 상세 정보 조회
        if (post != null) {
            PostResponse response = new PostResponse(
                post.getPostId(),
                post.getContent(),
                post.getImageUrls(),
                post.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME),
                post.getUpdatedAt() != null ? post.getUpdatedAt()
                    .format(DateTimeFormatter.ISO_DATE_TIME) : null
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build(); // 게시물 찾을 수 없을 때 404 반환
        }
    }

    // 게시물 수정하기
    @PostMapping("/edit/{id}")
    public ResponseEntity<Post> editPost(@PathVariable Long id,
        @RequestParam("content") String content,
        @RequestParam(value = "imageUrls", required = false) List<String> imageUrls) {
        PostRequest postRequest = new PostRequest();
        postRequest.setContent(content);

        if (imageUrls != null) {
            postRequest.setImageUrls(imageUrls); // 이미지 URL 설정
        }

        Post updatedPost = postService.updatePost(id, postRequest, new MultipartFile[]{});// 게시물 수정
        return ResponseEntity.ok(updatedPost);
    }

    // 게시물 삭제하기
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to delete post", e);
            return ResponseEntity.status(500).build(); // 삭제 실패 시 에러 반환
        }
    }
}
