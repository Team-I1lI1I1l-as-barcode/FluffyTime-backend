package com.fluffytime.like.controller.api.reply;

import com.fluffytime.domain.User;
import com.fluffytime.like.dto.reply.ReplyLikeRequestDto;
import com.fluffytime.like.dto.reply.ReplyLikeResponseDto;
import com.fluffytime.like.service.reply.ReplyLikeService;
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
    public ResponseEntity<ReplyLikeResponseDto> likeReply(
        @PathVariable(name = "replyId") Long replyId,
        @RequestBody ReplyLikeRequestDto requestDto) {
        //현재 사용자 ID
        User currentUser = replyLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        ReplyLikeResponseDto responseDto = replyLikeService.likeReply(replyId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //답글 좋아요 취소
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<ReplyLikeResponseDto> unlikeReply(
        @PathVariable(name = "replyId") Long replyId,
        @RequestBody ReplyLikeRequestDto requestDto) {
        //현재 사용자 ID
        User currentUser = replyLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        ReplyLikeResponseDto responseDto = replyLikeService.unlikeReply(replyId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //답글에 좋아요 한 유저 목록 조회
    @GetMapping("/reply/{replyId}/list")
    public ResponseEntity<List<ReplyLikeResponseDto>> getUsersWhoLikedReply(
        @PathVariable(name = "replyId") Long replyId) {
        List<ReplyLikeResponseDto> users = replyLikeService.getUsersWhoLikedReply(replyId);
        return ResponseEntity.ok(users);
    }
}
