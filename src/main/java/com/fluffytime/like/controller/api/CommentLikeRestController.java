package com.fluffytime.like.controller.api;

import com.fluffytime.domain.User;
import com.fluffytime.like.dto.CommentLikeRequestDto;
import com.fluffytime.like.dto.CommentLikeResponseDto;
import com.fluffytime.like.service.CommentLikeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class CommentLikeRestController {

    private final CommentLikeService commentLikeService;
    private final HttpServletRequest httpServletRequest;

    //댓글 좋아요 등록/좋아요 취소
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CommentLikeResponseDto> likeOrUnlikeComment(
        @PathVariable(name = "commentId") Long commentId,
        @RequestBody CommentLikeRequestDto requestDto) {

        //현재 사용자 ID
        User currentUser = commentLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        CommentLikeResponseDto responseDto = commentLikeService.likeOrUnlikeComment(commentId,
            requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //댓글에 좋아요를 한 유저 목록 조회
    @GetMapping("/comment/{commentId}/list")
    public ResponseEntity<List<CommentLikeResponseDto>> getUsersWhoLikedComment(
        @PathVariable(name = "commentId") Long commentId) {
        List<CommentLikeResponseDto> users = commentLikeService.getUsersWhoLikedComment(commentId);
        return ResponseEntity.ok(users); //유저 목록 반환
    }
}
