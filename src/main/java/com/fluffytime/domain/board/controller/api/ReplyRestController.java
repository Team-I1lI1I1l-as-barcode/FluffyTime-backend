package com.fluffytime.domain.board.controller.api;

import com.fluffytime.domain.board.dto.request.ReplyRequest;
import com.fluffytime.domain.board.dto.response.ReplyResponse;
import com.fluffytime.domain.board.entity.Reply;
import com.fluffytime.domain.board.exception.NotPermissionDelete;
import com.fluffytime.domain.board.exception.NotPermissionModify;
import com.fluffytime.domain.board.service.ReplyService;
import com.fluffytime.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
@RequestMapping("/api/replies")
@RequiredArgsConstructor
public class ReplyRestController {

    private final ReplyService replyService;

    //답글 저장
    @PostMapping("/reg")
    public ResponseEntity<Map<String, Object>> createReply(@RequestBody ReplyRequest requestDto,
        HttpServletRequest httpServletRequest) {
        User user = replyService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(user.getUserId());
        Reply savedReply = replyService.createReply(requestDto);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("replyId", savedReply.getReplyId());

        return ResponseEntity.ok().body(responseBody);
    }

    //답글 조회
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ReplyResponse>> getRepliesByCommentId(
        @PathVariable(name = "commentId") Long commentId, HttpServletRequest httpServletRequest) {

        User user = replyService.findByAccessToken(httpServletRequest);
        Long currentUserId = user.getUserId();
        List<ReplyResponse> replyList = replyService.getRepliesByCommentId(commentId,
            currentUserId);
        return ResponseEntity.ok(replyList);
    }

    //답글 수정
    @PutMapping("/update/{replyId}")
    public ResponseEntity<Void> updateReply(@PathVariable(name = "replyId") Long replyId,
        @RequestBody ReplyRequest request, HttpServletRequest httpServletRequest) {
        try {
            User user = replyService.findByAccessToken(httpServletRequest);
            Long currentUserId = user.getUserId();
            ReplyResponse reply = replyService.getReplyByReplyId(replyId, currentUserId);
            if (!reply.getUserId().equals(user.getUserId())) {
                throw new NotPermissionModify();
            }
            replyService.updateReply(replyId, request.getContent());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //답글 삭제
    @DeleteMapping("/delete/{replyId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "replyId") Long replyId,
        HttpServletRequest httpServletRequest) {
        try {
            User user = replyService.findByAccessToken(httpServletRequest);
            Long currentUserId = user.getUserId();
            ReplyResponse reply = replyService.getReplyByReplyId(replyId, currentUserId);
            if (!reply.getUserId().equals(user.getUserId())) {
                throw new NotPermissionDelete();
            }
            replyService.deleteReply(replyId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}