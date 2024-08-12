package com.fluffytime.reply.controller.api;

import com.fluffytime.reply.dto.ReplyRequestDto;
import com.fluffytime.reply.dto.ReplyResponseDto;
import com.fluffytime.reply.service.ReplyService;
import java.util.List;
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
    public ResponseEntity<Void> createReply(@RequestBody ReplyRequestDto requestDto) {
        replyService.createReply(requestDto);
        return ResponseEntity.ok().build();
    }

    //답글 조회
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ReplyResponseDto>> getRepliesByCommentId(
        @PathVariable(name = "commentId") Long commentId) {
        List<ReplyResponseDto> replyList = replyService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(replyList);
    }

    //답글 수정
    @PutMapping("/update/{replyId}")
    public ResponseEntity<Void> updateReply(@PathVariable(name = "replyId") Long replyId,
        @RequestBody ReplyRequestDto requestDto) {
        try {
            replyService.updateReply(replyId, requestDto.getContent());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //답글 삭제
    @DeleteMapping("/delete/{replyId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "replyId") Long replyId) {
        try {
            replyService.deleteReply(replyId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}