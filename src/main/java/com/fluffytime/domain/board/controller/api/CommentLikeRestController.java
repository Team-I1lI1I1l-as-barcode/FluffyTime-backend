package com.fluffytime.domain.board.controller.api;

import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.board.dto.request.CommentLikeRequest;
import com.fluffytime.domain.board.dto.response.CommentLikeResponse;
import com.fluffytime.domain.board.service.CommentLikeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Slf4j
public class CommentLikeRestController {

    private final CommentLikeService commentLikeService;
    private final HttpServletRequest httpServletRequest;

    //댓글 좋아요 등록
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CommentLikeResponse> likeComment(
        @PathVariable(name = "commentId") Long commentId,
        @RequestBody CommentLikeRequest requestDto) {
        //현재 사용자 ID
        User currentUser = commentLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        CommentLikeResponse responseDto = commentLikeService.likeComment(commentId,
            requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //댓글 좋아요 취소
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<CommentLikeResponse> unlikeComment(
        @PathVariable(name = "commentId") Long commentId,
        @RequestBody CommentLikeRequest requestDto) {
        //현재 사용자 ID
        User currentUser = commentLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        CommentLikeResponse responseDto = commentLikeService.unlikeComment(commentId,
            requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //댓글에 좋아요를 한 유저 목록 조회
    @GetMapping("/comment/{commentId}/list")
    public ResponseEntity<List<CommentLikeResponse>> getUsersWhoLikedComment(
        @PathVariable(name = "commentId") Long commentId) {
        List<CommentLikeResponse> users = commentLikeService.getUsersWhoLikedComment(commentId);
        return ResponseEntity.ok(users); //유저 목록 반환
    }
}
