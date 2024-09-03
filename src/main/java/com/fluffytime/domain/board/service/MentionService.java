package com.fluffytime.domain.board.service;

import com.fluffytime.domain.board.dto.request.MentionRequest;
import com.fluffytime.domain.board.dto.response.MentionResponse;
import com.fluffytime.domain.board.entity.Comment;
import com.fluffytime.domain.board.entity.Mention;
import com.fluffytime.domain.board.entity.Post;
import com.fluffytime.domain.board.entity.Reply;
import com.fluffytime.domain.board.repository.CommentRepository;
import com.fluffytime.domain.board.repository.MentionRepository;
import com.fluffytime.domain.board.repository.PostRepository;
import com.fluffytime.domain.board.repository.ReplyRepository;
import com.fluffytime.domain.notification.service.NotificationService;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.common.exception.global.CommentNotFound;
import com.fluffytime.global.common.exception.global.PostNotFound;
import com.fluffytime.global.common.exception.global.ReplyNotFound;
import com.fluffytime.global.common.exception.global.UserNotFound;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentionService {

    private final UserRepository userRepository;
    private final MentionRepository mentionRepository;
    private final NotificationService notificationService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    // 멘션 생성
    @Transactional
    public MentionResponse handleMentions(MentionRequest requestDto) {
        // 로그 출력
        log.info("Received content: {}", requestDto.getContent());

        Post post = requestDto.getPostId() != null ? postRepository.findById(requestDto.getPostId())
            .orElseThrow(PostNotFound::new) : null;

        Comment comment = requestDto.getCommentId() != null ? commentRepository.findById(
                requestDto.getCommentId())
            .orElseThrow(CommentNotFound::new) : null;

        Reply reply =
            requestDto.getReplyId() != null ? replyRepository.findById(requestDto.getReplyId())
                .orElseThrow(ReplyNotFound::new) : null;

        // 멘션 대상이 하나도 없는 경우 예외 처리
        if (post == null && comment == null && reply == null) {
            throw new IllegalArgumentException(
                "Unsupported mention target: No valid target (Post, Comment, or Reply) provided.");
        }

        // 텍스트에서 멘션 파싱
        String content = requestDto.getContent(); // 텍스트에서 멘션을 포함한 전체 내용
        log.info("Parsing content for mentions: {}", content); // 추가 로그 출력
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(content);

        Mention lastMention = null; // 마지막으로 생성된 멘션을 저장하기 위한 변수
        boolean mentionCreated = false; // 멘션 생성 여부를 체크하는 변수

        while (matcher.find()) {
            String nickname = matcher.group(1);
            log.info("Found mention nickname: {}", nickname); // 추가 로그 출력
            User mentionedUser = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFound::new);

            Mention mention = Mention.builder()
                .post(post)
                .comment(comment)
                .reply(reply)
                .metionedUser(mentionedUser)
                .build();

            mentionRepository.save(mention);
            lastMention = mention; // 마지막 멘션을 저장
            mentionCreated = true;

            // 알림 생성
            notificationService.createMentionNotification(mention);
        }

        // 마지막으로 생성된 멘션이 없는 경우 적절한 처리
        if (!mentionCreated) {
            log.warn("No mentions were created from the content: {}", content); // 경고 로그 출력
            throw new IllegalStateException("No mentions were created.");
        }

        return convertToMentionResponse(lastMention);
    }

    // 멘션 response convert
    private MentionResponse convertToMentionResponse(Mention mention) {
        if (mention == null) {
            throw new IllegalArgumentException("Mention cannot be null.");
        }

        return MentionResponse.builder()
            .mentionId(mention.getMentionId())
            .postId(mention.getPost() != null ? mention.getPost().getPostId() : null)
            .commentId(mention.getComment() != null ? mention.getComment().getCommentId() : null)
            .replyId(mention.getReply() != null ? mention.getReply().getReplyId() : null)
            .mentionedUserId(
                mention.getMetionedUser() != null ? mention.getMetionedUser().getUserId() : null)
            .mentionedNickname(
                mention.getMetionedUser() != null ? mention.getMetionedUser().getNickname() : null)
            .build();
    }
}
