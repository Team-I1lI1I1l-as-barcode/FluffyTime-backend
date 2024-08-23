package com.fluffytime.user.controller.api;

import com.fluffytime.user.dto.request.JoinRequest;
import com.fluffytime.user.dto.request.LoginUser;
import com.fluffytime.user.dto.response.CheckDuplicationResponse;
import com.fluffytime.user.dto.response.JoinResponse;
import com.fluffytime.user.dto.response.SucceedCertificationResponse;
import com.fluffytime.user.dto.response.SucceedSendEmailResponse;
import com.fluffytime.user.service.CertificationService;
import com.fluffytime.user.service.JoinService;
import com.fluffytime.user.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserApiController {

    private final JoinService joinService;
    private final LoginService loginService;
    private final CertificationService certificationService;

    // 임시 회원 가입 (redis에 회원 정보 임시 저장)
    @PostMapping("/temp-join")
    public ResponseEntity<JoinResponse> tempJoin(
        @RequestBody @Valid JoinRequest joinUser
    ) {
        return ResponseEntity.created(URI.create("/join/email-certificate/" + joinUser.getEmail()))
            .body(joinService.tempJoin(joinUser));
    }

    // 간편 회원가입
    @PostMapping("/social-join")
    public ResponseEntity<JoinResponse> socialJoin(
        @RequestBody @Valid JoinRequest joinUser
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(joinService.socialJoin(joinUser));
    }

    // 회원가입
    @GetMapping("/join")
    public ResponseEntity<JoinResponse> join(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(joinService.join(email));
    }



    // 로그인
    @PostMapping("login")
    public ResponseEntity<Void> login(
        @RequestBody @Valid LoginUser loginUser,
        @RequestParam(defaultValue = "/", name = "redirectURL") String redirectURL,
        HttpServletResponse response
    ) {
        loginService.loginProcess(response,loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectURL);

        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(headers)
            .build();
    }

    // 로그아웃
    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        return loginService.logoutProcess(request, response);
    }

    // 이메일 중복확인
    @GetMapping("/check-email")
    public ResponseEntity<CheckDuplicationResponse> checkEmail(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(joinService.checkExistsEmail(email));
    }

    // 유저명 중복확인
    @GetMapping("/check-nickname")
    public ResponseEntity<CheckDuplicationResponse> checkNickname(
        @RequestParam(name = "nickname")
        @NotBlank
        String nickname
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(joinService.checkExistsNickname(nickname));
    }

    // 인증 메일 전송
    @GetMapping("/email-certification/send")
    public ResponseEntity<SucceedSendEmailResponse> sendCertification(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(certificationService.sendCertificationMail(email));
    }

    // 메일 인증
    @GetMapping("/email-certification")
    public ResponseEntity<SucceedCertificationResponse> certificateEmail(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(certificationService.certificateEmail(email));
    }
}
