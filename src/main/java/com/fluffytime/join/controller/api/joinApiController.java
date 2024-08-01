package com.fluffytime.join.controller.api;

import com.fluffytime.join.reponse.JoinResponseDto;
import com.fluffytime.join.reponse.ResponseDto;
import com.fluffytime.join.request.JoinRequestDto;
import com.fluffytime.join.service.JoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class joinApiController {

    private final JoinService joinService;

    @PostMapping("/api/users/join")
    public ResponseEntity<ResponseDto<JoinResponseDto>> join(
        @RequestBody @Valid JoinRequestDto joinUser
    ) {
        log.info("user = {}", joinUser.toString());
        ResponseDto<JoinResponseDto> response = joinService.join(joinUser);
        return ResponseEntity.ok(response);
    }
}
