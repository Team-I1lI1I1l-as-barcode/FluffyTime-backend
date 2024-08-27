package com.fluffytime.domain.board.controller.api;

import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.board.dto.request.PostLikeRequest;
import com.fluffytime.domain.board.dto.response.PostLikeResponse;
import com.fluffytime.domain.board.service.PostLikeService;
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
public class PostLikeRestController {

    private final PostLikeService postLikeService;
    private final HttpServletRequest httpServletRequest;

    //게시글 좋아요 등록
    @PostMapping("/post/{postId}")
    public ResponseEntity<PostLikeResponse> likePost(
        @PathVariable(name = "postId") Long postId,
        @RequestBody PostLikeRequest requestDto) {
        //현재 사용자 ID
        User currentUser = postLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        PostLikeResponse responseDto = postLikeService.likePost(postId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //게시글 좋아요 취소
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<PostLikeResponse> unlikePost(
        @PathVariable(name = "postId") Long postId,
        @RequestBody PostLikeRequest requestDto) {
        //현재 사용자 ID
        User currentUser = postLikeService.findByAccessToken(httpServletRequest);
        requestDto.setUserId(currentUser.getUserId());

        PostLikeResponse responseDto = postLikeService.unlikePost(postId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //게시글에 좋아요를 한 유저 목록 조회
    @GetMapping("/post/{postId}/list")
    public ResponseEntity<List<PostLikeResponse>> getUsersWhoLikedPost(
        @PathVariable(name = "postId") Long postId) {
        List<PostLikeResponse> users = postLikeService.getUsersWhoLikedPost(postId);
        return ResponseEntity.ok(users); //유저 목록 반환
    }
}
