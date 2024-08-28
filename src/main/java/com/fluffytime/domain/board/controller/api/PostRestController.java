package com.fluffytime.domain.board.controller.api;

import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.board.dto.request.PostRequest;
import com.fluffytime.domain.board.dto.response.PostResponse;
import com.fluffytime.domain.board.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;

    // 게시물 등록
    @PostMapping("/reg")
    public ResponseEntity<Long> regPost(@RequestPart("post") PostRequest postRequest,
        @RequestPart(value = "images", required = false) MultipartFile[] files,
        HttpServletRequest request) {
        log.info("게시물 등록 요청 받음: {}", postRequest);

        if (postRequest.getTempId() != null) {
            // 임시 저장된 글 최종 등록 시 이미지 추가/수정 불가
            files = null;
        }

        Long postId = postService.createPost(postRequest, files, request);

        if (postRequest.getTempId() != null) {
            postService.deleteTempPost(postRequest.getTempId());
        }

        log.info("게시물 등록 성공, ID: {}", postId);
        return ResponseEntity.status(HttpStatus.OK).body(postId);
    }

    // 임시 게시물 등록
    @PostMapping("/temp-reg")
    public ResponseEntity<Long> tempRegPost(@RequestPart("post") PostRequest postRequest,
        @RequestPart("images") MultipartFile[] files,
        HttpServletRequest request) {
        log.info("임시 게시물 등록 요청 받음: {}", postRequest);

        Long postId = postService.createTempPost(postRequest, files, request);

        log.info("임시 게시물 등록 성공, ID: {}", postId);
        return ResponseEntity.status(HttpStatus.OK).body(postId);
    }

    // 임시 게시물 삭제
    @PostMapping("/temp-delete/{id}")
    public ResponseEntity<Void> deleteTempPost(@PathVariable(name = "id") Long id) {
        log.info("임시 게시물 삭제 요청 받음, ID: {}", id);
        postService.deleteTempPost(id);
        log.info("임시 게시물 삭제 성공, ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 임시 게시물 목록 조회
    @GetMapping("/temp-posts/list")
    public ResponseEntity<List<PostResponse>> getTempPosts(HttpServletRequest httpServletRequest) {
        User user = postService.findUserByAccessToken(httpServletRequest);
        Long currrentUserId = user.getUserId();
        log.info("임시 게시물 목록 조회 요청 받음");
        List<PostResponse> tempPosts = postService.getTempPosts(currrentUserId);
        log.info("임시 게시물 목록 조회 성공, 개수: {}", tempPosts.size());
        return ResponseEntity.status(HttpStatus.OK).body(tempPosts);
    }

    // 게시물 상세 정보 조회
    @GetMapping("/detail/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable(name = "id") Long id, HttpServletRequest httpServletRequest) {
        User user = postService.findUserByAccessToken(httpServletRequest);
        Long currentUserId = user.getUserId();
        log.info("게시물 상세 정보 조회 요청 받음, ID: {}", id);
        PostResponse postResponse = postService.getPostById(id, currentUserId);
        log.info("게시물 상세 정보 조회 성공, ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(postResponse);
    }

    // 게시물 수정
    @PostMapping("/edit/{id}")
    public ResponseEntity<PostResponse> editPost(@PathVariable(name = "id") Long id,
        @RequestPart(value = "post") PostRequest postRequest,
        @RequestPart(value = "files", required = false) MultipartFile[] files,
        HttpServletRequest request) {

        User user = postService.findUserByAccessToken(request);
        Long currentUserId = user.getUserId();

        log.info("게시물 수정 요청 받음, ID: {}", id);

        PostResponse updatedPostResponse = postService.updatePost(id, postRequest, files, request,
            currentUserId);
        log.info("게시물 수정 성공, ID: {}", updatedPostResponse.getPostId());
        return ResponseEntity.status(HttpStatus.OK).body(updatedPostResponse);
    }

    // 게시물 삭제
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable(name = "id") Long id,
        HttpServletRequest request) {
        log.info("게시물 삭제 요청 받음, ID: {}", id);
        postService.deletePost(id, request);
        log.info("게시물 삭제 성공, ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 댓글 기능 설정
    @PostMapping("/toggle-comments/{id}")
    public ResponseEntity<Void> toggleComments(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        User user = postService.findUserByAccessToken(request);
        postService.toggleComments(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //현재 사용자가 게시물의 작성자인지 확인
    @GetMapping("/is-author/{id}")
    public ResponseEntity<Map<String, Boolean>> isAuthor(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        User user = postService.findUserByAccessToken(request);
        boolean isAuthor = postService.checkIfUserIsAuthor(id, user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuthor", isAuthor);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
