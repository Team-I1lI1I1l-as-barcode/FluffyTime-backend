package com.fluffytime.domain.board.controller.api;

import com.fluffytime.domain.board.dto.request.MentionRequest;
import com.fluffytime.domain.board.dto.response.MentionResponse;
import com.fluffytime.domain.board.service.MentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mentions")
public class MentionRestController {

    private final MentionService mentionService;

    @PostMapping("/reg")
    public ResponseEntity<MentionResponse> createMention(@RequestBody MentionRequest requestDto) {
        MentionResponse responseDto = mentionService.handleMentions(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
