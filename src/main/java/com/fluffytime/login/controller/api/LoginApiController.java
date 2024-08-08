package com.fluffytime.login.controller.api;

import com.fluffytime.login.dto.request.LoginUser;
import com.fluffytime.login.dto.response.ApiResponse;
import com.fluffytime.login.dto.response.UserLoginResponse;
import com.fluffytime.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class LoginApiController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(
        @RequestBody @Valid LoginUser loginUser, HttpServletResponse response) {
        log.info("loginuser = {}", loginUser.toString());
        return ResponseEntity.status(HttpStatus.OK).body(loginService.verifyUser(loginUser, response));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<UserLoginResponse> refreshToken(HttpServletRequest request,
        HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(loginService.getRefreshToken(request, response));
    }
}
