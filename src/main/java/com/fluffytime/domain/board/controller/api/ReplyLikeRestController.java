package com.fluffytime.domain.board.controller.api;

import com.fluffytime.domain.board.dto.request.ReplyLikeRequest;
import com.fluffytime.domain.board.dto.response.ReplyLikeResponse;
import com.fluffytime.domain.board.service.ReplyLikeService;
import com.fluffytime.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class ReplyLikeRestController {

    private final ReplyLikeService replyLikeService;
    private final HttpServletRequest httpServletRequest;

    //답글 좋아요 등록
    @PostMapping("/reply/{replyId}")
    public ResponseEntity<ReplyLikeResponse> likeReply(
        @PathVariable(name = "replyId") Long replyId,
        @RequestBody ReplyLikeRequest requestDto) {
        //현재 사용자 ID
        User currentUser = replyLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        ReplyLikeResponse responseDto = replyLikeService.likeReply(replyId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //답글 좋아요 취소
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<ReplyLikeResponse> unlikeReply(
        @PathVariable(name = "replyId") Long replyId,
        @RequestBody ReplyLikeRequest requestDto) {
        //현재 사용자 ID
        User currentUser = replyLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        ReplyLikeResponse responseDto = replyLikeService.unlikeReply(replyId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //답글에 좋아요 한 유저 목록 조회
    @GetMapping("/reply/{replyId}/list")
    public ResponseEntity<List<ReplyLikeResponse>> getUsersWhoLikedReply(
        @PathVariable(name = "replyId") Long replyId) {
        List<ReplyLikeResponse> users = replyLikeService.getUsersWhoLikedReply(replyId);
        return ResponseEntity.ok(users);
    }
}
