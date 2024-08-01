package com.fluffytime.comment.controller.api;

import com.fluffytime.comment.dto.CommentRequestDto;
import com.fluffytime.comment.dto.CommentResponseDto;
import com.fluffytime.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<CommentResponseDto> createComment(
        @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto responseDto = commentService.createComment(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
