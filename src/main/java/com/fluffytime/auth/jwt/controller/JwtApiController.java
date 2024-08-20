package com.fluffytime.auth.jwt.controller;

import com.fluffytime.auth.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class JwtApiController {

    private final JwtService jwtService;

    // refresh token으로 access token 재발급
    @PostMapping("/refreshToken")
    public ResponseEntity accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.reissue(request, response);
    }
}
