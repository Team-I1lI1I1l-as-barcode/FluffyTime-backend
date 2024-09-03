package com.fluffytime.domain.board.controller.api;

import com.fluffytime.domain.board.dto.response.ReelsResponse;
import com.fluffytime.domain.board.service.ReelsService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reels")
@RequiredArgsConstructor
public class ReelsRestController {

    private final ReelsService reelsService;

    // 모든 릴스를 반환하는 엔드포인트
    @GetMapping
    public ResponseEntity<List<ReelsResponse>> listReels(HttpServletRequest request) {
        List<ReelsResponse> reelsList = reelsService.getAllReels(request);
        return new ResponseEntity<>(reelsList, HttpStatus.OK);
    }

}