package com.fluffytime.comment.controller.api;

import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.comment.dto.CommentResponseDto;
import com.fluffytime.comment.service.CommentService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    //댓글 등록
    @PostMapping("/reg")
    public ResponseEntity<Map<String, String>> createComment(
        @Valid @RequestBody CommentRequestDto requestDto) {
        try {
            commentService.createComment(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "댓글 등록 성공!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("message", "댓글 등록 실패!: " + e.getMessage()));
        }
    }

    //댓글 조회(게시글마다)
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentByPostId(
        @PathVariable(name = "postId") Long postId) {
        List<CommentResponseDto> commentList = commentService.getCommentByPostId(postId);
        return ResponseEntity.ok(commentList);
    }

    //댓글 수정
    @PutMapping("/update/{commentId}")
    public ResponseEntity<Void> updateComment(
        @PathVariable(name = "commentId") Long commentId, @RequestBody CommentRequestDto request) {
        try {
            commentService.updateComment(commentId, request.getContent());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "commentId") Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
