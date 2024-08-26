package com.fluffytime.like.controller.api.post;

import com.fluffytime.domain.User;
import com.fluffytime.like.dto.post.PostLikeRequestDto;
import com.fluffytime.like.dto.post.PostLikeResponseDto;
import com.fluffytime.like.service.post.PostLikeService;
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
public class PostLikeRestController {

    private final PostLikeService postLikeService;
    private final HttpServletRequest httpServletRequest;

    //게시글 좋아요 등록/취소
    @PostMapping("/post/{postId}")
    public ResponseEntity<PostLikeResponseDto> likeOrUnlikePost(
        @PathVariable(name = "postId") Long postId,
        @RequestBody PostLikeRequestDto requestDto) {
        //현재 사용자 ID
        User currentUser = postLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        PostLikeResponseDto responseDto = postLikeService.likeOrUnlikePost(postId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //게시글에 좋아요를 한 유저 목록 조회
    @GetMapping("/post/{postId}/list")
    public ResponseEntity<List<PostLikeResponseDto>> getUsersWhoLikedPost(
        @PathVariable(name = "postId") Long postId) {
        List<PostLikeResponseDto> users = postLikeService.getUsersWhoLikedPost(postId);
        return ResponseEntity.ok(users); //유저 목록 반환
    }
}
