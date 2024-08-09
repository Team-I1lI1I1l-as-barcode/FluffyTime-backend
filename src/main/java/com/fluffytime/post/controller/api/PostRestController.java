package com.fluffytime.post.controller.api;

import com.fluffytime.domain.Post;
import com.fluffytime.post.dto.request.PostRequest;
import com.fluffytime.post.dto.response.ApiResponse;
import com.fluffytime.post.dto.response.PostResponse;
import com.fluffytime.post.dto.response.PostResponseCode;
import com.fluffytime.post.service.PostService;
import java.net.URI;
import java.time.format.DateTimeFormatter;
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

    // 게시물 등록 및 임시 저장된 글 삭제
    @PostMapping("/reg")
    public ResponseEntity<ApiResponse<Long>> regPost(@RequestPart("post") PostRequest postRequest,
        @RequestPart("images") MultipartFile[] files) {
        log.info("게시물 등록 요청 받음: {}", postRequest);
        ApiResponse<Long> postIdResponse = postService.createPost(postRequest, files);

        if (postRequest.getTempId() != null) {
            postService.deleteTempPost(postRequest.getTempId());
        }

        log.info("게시물 등록 성공, ID: {}", postIdResponse.getData());
        return ResponseEntity.created(URI.create("/api/posts/" + postIdResponse.getData()))
            .body(postIdResponse);
    }

    // 게시물 임시등록
    @PostMapping("/temp-reg")
    public ResponseEntity<ApiResponse<Long>> tempRegPost(
        @RequestPart("post") PostRequest postRequest,
        @RequestPart("images") MultipartFile[] files) {
        log.info("임시 게시물 등록 요청 받음: {}", postRequest);
        ApiResponse<Long> postIdResponse = postService.createTempPost(postRequest, files);
        log.info("임시 게시물 등록 성공, ID: {}", postIdResponse.getData());
        return ResponseEntity.created(URI.create("/api/posts/" + postIdResponse.getData()))
            .body(postIdResponse);
    }

    // 임시 등록 게시물 삭제하기
    @PostMapping("/temp-delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTempPost(@PathVariable Long id) {
        log.info("임시 게시물 삭제 요청 받음, ID: {}", id);
        ApiResponse<Void> response = postService.deleteTempPost(id);
        log.info("임시 게시물 삭제 성공, ID: {}", id);
        return ResponseEntity.ok(response);
    }

    // 임시 등록 게시물 목록 불러오기
    @GetMapping("/temp-posts/list")
    public ResponseEntity<ApiResponse<List<Post>>> getTempPosts() {
        log.info("임시 게시물 목록 조회 요청 받음");
        ApiResponse<List<Post>> tempPostsResponse = postService.getTempPosts();
        log.info("임시 게시물 목록 조회 성공, 개수: {}", tempPostsResponse.getData().size());
        return ResponseEntity.ok(tempPostsResponse);
    }

    // 게시물 상세 정보 조회하기
    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long id) {
        log.info("게시물 상세 정보 조회 요청 받음, ID: {}", id);
        ApiResponse<Post> postResponse = postService.getPostById(id);
        Post post = postResponse.getData();
        PostResponse response = new PostResponse(
            post.getPostId(),
            post.getContent(),
            post.getImageUrls(),
            post.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME),
            post.getUpdatedAt() != null ? post.getUpdatedAt()
                .format(DateTimeFormatter.ISO_DATE_TIME) : null
        );
        log.info("게시물 상세 정보 조회 성공, ID: {}", id);
        return ResponseEntity.ok(ApiResponse.response(PostResponseCode.GET_POST_SUCCESS, response));
    }

    // 게시물 수정하기
    @PostMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<Post>> editPost(@PathVariable Long id,
        @RequestParam("content") String content,
        @RequestParam(value = "imageUrls", required = false) List<String> imageUrls,
        @RequestPart(value = "files", required = false) MultipartFile[] files) {
        log.info("게시물 수정 요청 받음, ID: {}", id);
        PostRequest postRequest = new PostRequest();
        postRequest.setContent(content);
        if (imageUrls != null) {
            postRequest.setImageUrls(imageUrls);
        }

        ApiResponse<Post> updatedPostResponse = postService.updatePost(id, postRequest, files);
        log.info("게시물 수정 성공, ID: {}", updatedPostResponse.getData().getPostId());
        return ResponseEntity.ok(updatedPostResponse);
    }

    // 게시물 삭제하기
    @PostMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        log.info("게시물 삭제 요청 받음, ID: {}", id);
        ApiResponse<Void> response = postService.deletePost(id);
        log.info("게시물 삭제 성공, ID: {}", id);
        return ResponseEntity.ok(response);
    }
}
