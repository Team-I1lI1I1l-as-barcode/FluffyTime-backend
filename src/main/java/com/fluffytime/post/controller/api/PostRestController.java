package com.fluffytime.post.controller.api;

import com.fluffytime.domain.User;
import com.fluffytime.post.dto.request.PostRequest;
import com.fluffytime.post.dto.response.ApiResponse;
import com.fluffytime.post.dto.response.PostResponse;
import com.fluffytime.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;

    // 게시물 등록 및 임시 저장된 글 삭제
    @PostMapping("/reg")
    public ResponseEntity<?> regPost(@RequestPart("post") PostRequest postRequest,
        @RequestPart("images") MultipartFile[] files,
        HttpServletRequest request) {
        log.info("게시물 등록 요청 받음: {}", postRequest);

        // PostService에서 액세스 토큰을 통해 사용자 정보 추출
        User user = postService.findUserByAccessToken(request);
        log.info("Access Token으로 사용자 정보 추출: {}", user);

        // 게시물 생성 후 ID 반환
        ApiResponse<Long> postIdResponse = postService.createPost(postRequest, files, request);

        // 임시 저장된 게시글이 있으면 삭제
        if (postRequest.getTempId() != null) {
            postService.deleteTempPost(postRequest.getTempId());
        }

        log.info("게시물 등록 성공, ID: {}", postIdResponse.getData());
        return ResponseEntity.status(HttpStatus.OK)
            .body(postIdResponse);
    }

    // 게시물 임시 등록
    @PostMapping("/temp-reg")
    public ResponseEntity<?> tempRegPost(@RequestPart("post") PostRequest postRequest,
        @RequestPart("images") MultipartFile[] files,
        HttpServletRequest request) {
        log.info("임시 게시물 등록 요청 받음: {}", postRequest);

        // PostService에서 액세스 토큰을 통해 사용자 정보 추출
        User user = postService.findUserByAccessToken(request);
        log.info("Access Token으로 사용자 정보 추출: {}", user);

        // 임시 게시물 생성 후 ID 반환
        ApiResponse<Long> postIdResponse = postService.createTempPost(postRequest, files, request);

        log.info("임시 게시물 등록 성공, ID: {}", postIdResponse.getData());
        return ResponseEntity.status(HttpStatus.OK)
            .body(postIdResponse);
    }

    // 임시 등록 게시물 삭제하기
    @PostMapping("/temp-delete/{id}")
    public ResponseEntity<?> deleteTempPost(@PathVariable Long id) {
        log.info("임시 게시물 삭제 요청 받음, ID: {}", id);
        ApiResponse<Void> response = postService.deleteTempPost(id);
        log.info("임시 게시물 삭제 성공, ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }

    // 임시 등록 게시물 목록 불러오기
    @GetMapping("/temp-posts/list")
    public ResponseEntity<?> getTempPosts() {
        log.info("임시 게시물 목록 조회 요청 받음");
        ApiResponse<List<PostResponse>> tempPostsResponse = postService.getTempPosts();
        log.info("임시 게시물 목록 조회 성공, 개수: {}", tempPostsResponse.getData().size());
        return ResponseEntity.status(HttpStatus.OK)
            .body(tempPostsResponse);
    }

    // 게시물 상세 정보 조회하기
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        log.info("게시물 상세 정보 조회 요청 받음, ID: {}", id);
        ApiResponse<PostResponse> postResponse = postService.getPostById(id);
        log.info("게시물 상세 정보 조회 성공, ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(postResponse);
    }

    // 게시물 수정하기
    @PostMapping("/edit/{id}")
    public ResponseEntity<?> editPost(@PathVariable Long id,
        @RequestParam("content") String content,
        @RequestPart(value = "files", required = false) MultipartFile[] files,
        HttpServletRequest request) {
        log.info("게시물 수정 요청 받음, ID: {}", id);

        // PostService에서 액세스 토큰을 통해 사용자 정보 추출
        User user = postService.findUserByAccessToken(request);
        log.info("Access Token으로 사용자 정보 추출: {}", user);

        PostRequest postRequest = new PostRequest();
        postRequest.setContent(content);

        // 게시물 수정 후 응답 반환
        ApiResponse<PostResponse> updatedPostResponse = postService.updatePost(id, postRequest,
            files);
        log.info("게시물 수정 성공, ID: {}", updatedPostResponse.getData().getPostId());
        return ResponseEntity.status(HttpStatus.OK)
            .body(updatedPostResponse);
    }

    // 게시물 삭제하기
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, HttpServletRequest request) {
        log.info("게시물 삭제 요청 받음, ID: {}", id);

        // PostService에서 액세스 토큰을 통해 사용자 정보 추출
        User user = postService.findUserByAccessToken(request);
        log.info("Access Token으로 사용자 정보 추출: {}", user);

        // 게시물 삭제 후 응답 반환
        ApiResponse<Void> response = postService.deletePost(id);
        log.info("게시물 삭제 성공, ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }
}
