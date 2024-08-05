package com.fluffytime.comment.controller.api;

import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.comment.service.CommentService;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping("/reg")
    public ResponseEntity<Map<String, String>> createComment(
        @RequestBody CommentRequestDto requestDto) {
        try {
            commentService.createComment(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "댓글 등록 성공!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("message", "댓글 등록 실패!: " + e.getMessage()));
        }
    }
}
