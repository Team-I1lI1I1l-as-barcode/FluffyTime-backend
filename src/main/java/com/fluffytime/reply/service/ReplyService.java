package com.fluffytime.reply.service;

import com.fluffytime.common.exception.global.NotFoundComment;
import com.fluffytime.common.exception.global.NotFoundReply;
import com.fluffytime.common.exception.global.NotFoundUser;
import com.fluffytime.domain.Comment;
import com.fluffytime.domain.Reply;
import com.fluffytime.domain.User;
import com.fluffytime.reply.dto.ReplyRequestDto;
import com.fluffytime.reply.dto.ReplyResponseDto;
import com.fluffytime.repository.CommentRepository;
import com.fluffytime.repository.ReplyRepository;
import com.fluffytime.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    //답글 저장
    public void createReply(ReplyRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(NotFoundUser::new);
        Comment comment = commentRepository.findById(requestDto.getCommentId())
            .orElseThrow(NotFoundComment::new); //임시 예외처리

        Reply reply = Reply.builder()
            .content(requestDto.getContent())
            .user(user)
            .comment(comment)
            .build();
        replyRepository.save(reply);
    }

    //답글 조회
    public List<ReplyResponseDto> getRepliesByCommentId(Long commentId) {
        List<Reply> replyList = replyRepository.findByCommentCommentId(commentId);
        return replyList.stream()
            .map(ReplyResponseDto::new)
            .collect(Collectors.toList());
    }

    //답글 수정
    public void updateReply(Long replyId, String content) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(NotFoundReply::new);
        reply.setContent(content);
        replyRepository.save(reply);
    }

    //답글 삭제
    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(NotFoundReply::new);
        replyRepository.delete(reply);
    }
}
