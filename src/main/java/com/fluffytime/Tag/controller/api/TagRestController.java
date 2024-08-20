//package com.fluffytime.Tag.controller.api;
//
//import com.fluffytime.post.dto.request.PostRequest;
//import com.fluffytime.post.dto.response.ApiResponse;
//import com.fluffytime.post.dto.response.PostResponse;
//import com.fluffytime.post.service.PostService;
//import jakarta.servlet.http.HttpServletRequest;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/posts/tags")
//@RequiredArgsConstructor
//public class TagRestController {
//
//    private final PostService postService;
//
//    // 태그 등록 (게시물 생성과 함께)
//    @PostMapping("/reg")
//    public ResponseEntity<ApiResponse<?>> registerTag(
//        @RequestParam Long postId,
//        @RequestParam String content,
//        @RequestParam List<Long> tagIds,
//        @RequestParam MultipartFile[] files,
//        HttpServletRequest request) {
//
//        PostRequest postRequest = new PostRequest();
//        postRequest.setTempId(postId);
//        postRequest.setContent(content);
//        postRequest.setTagId(tagIds);
//
//        ApiResponse<Long> response = postService.createPost(postRequest, files, request);
//        return ResponseEntity.ok(response);
//    }
//
//    // 태그 수정 (게시물 수정과 함께)
//    @PostMapping("/edit")
//    public ResponseEntity<ApiResponse<?>> editTag(
//        @RequestParam Long postId,
//        @RequestParam String content,
//        @RequestParam List<Long> tagIds,
//        @RequestParam MultipartFile[] files) {
//
//        PostRequest postRequest = new PostRequest();
//        postRequest.setContent(content);
//        postRequest.setTagId(tagIds);
//
//        ApiResponse<PostResponse> response = postService.updatePost(postId, postRequest, files);
//        return ResponseEntity.ok(response);
//    }
//
//    // 태그 삭제 (게시물 삭제와 함께)
//    @PostMapping("/delete")
//    public ResponseEntity<ApiResponse<?>> deleteTag(@RequestParam Long postId) {
//        ApiResponse<Void> response = postService.deletePost(postId);
//        return ResponseEntity.ok(response);
//    }
//}