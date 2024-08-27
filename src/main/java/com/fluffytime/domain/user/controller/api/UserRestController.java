package com.fluffytime.domain.user.controller.api;

import com.fluffytime.domain.user.dto.request.FindEmailRequest;
import com.fluffytime.domain.user.dto.request.LoginUserRequest;
import com.fluffytime.domain.user.dto.request.PasswordChangeRequest;
import com.fluffytime.domain.user.dto.request.SendEmailRequest;
import com.fluffytime.domain.user.dto.response.FindEmailResponse;
import com.fluffytime.domain.user.service.JoinService;
import com.fluffytime.global.common.exception.global.BadRequest;
import com.fluffytime.global.common.exception.global.UserNotFound;
import com.fluffytime.global.common.smtp.builder.CertificationEmailContent;
import com.fluffytime.global.common.smtp.builder.ChangePasswordEmailContent;
import com.fluffytime.global.common.smtp.service.EmailService;
import com.fluffytime.domain.user.dto.request.JoinRequest;
import com.fluffytime.domain.user.dto.response.CheckDuplicationResponse;
import com.fluffytime.domain.user.dto.response.JoinResponse;
import com.fluffytime.domain.user.dto.response.SucceedCertificationResponse;
import com.fluffytime.domain.user.dto.response.SucceedSendEmailResponse;
import com.fluffytime.domain.user.service.LoginService;
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
public class UserRestController {

    private final EmailService emailService;
    private final JoinService joinService;
    private final LoginService loginService;

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
        @RequestBody @Valid LoginUserRequest loginUser,
        @RequestParam(defaultValue = "/", name = "redirectURL") String redirectURL,
        HttpServletResponse response
    ) {
        loginService.loginProcess(response, loginUser);

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
        String subject = "[ FluffyTime - 반려동물 전용 SNS ] 가입 인증 메일입니다.";

        return ResponseEntity.status(HttpStatus.OK)
            .body(emailService.sendHtmlMail(email,subject,new CertificationEmailContent()));
    }

    // 메일 인증
    @GetMapping("/email-certification")
    public ResponseEntity<SucceedCertificationResponse> certificateEmail(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(joinService.certificateEmail(email));
    }

    @PostMapping("/find-email")
    public ResponseEntity<FindEmailResponse> findEmail(
        @RequestBody @Valid FindEmailRequest findEmailRequest
    ) {
        log.info("Dto ={}",findEmailRequest);
        return loginService.findEmail(findEmailRequest);
    }

    // 비밀번호 변경 메일 전송
    @PostMapping("/email-changePassword/send")
    public ResponseEntity<SucceedSendEmailResponse> sendChangePasswordMail(
        @RequestBody @Valid SendEmailRequest sendEmailRequest
    ) {
        if(loginService.existsUserByEmail(sendEmailRequest.getEmail())) {
            String subject = "[ FluffyTime - 반려동물 전용 SNS ] 비밀번호 변경 메일입니다.";

            loginService.savePasswordChangeTtl(sendEmailRequest.getEmail());

            return ResponseEntity.status(HttpStatus.OK)
                .body(emailService.sendHtmlMail(sendEmailRequest.getEmail(),subject,new ChangePasswordEmailContent()));
        } else {
            throw new UserNotFound();
        }
    }

    @PostMapping("/change/password")
    public ResponseEntity<Void> changePassword(
        @RequestBody @Valid PasswordChangeRequest passwordChangeRequest
    ) {
        if (loginService.findPasswordChangeTtl(passwordChangeRequest.getEmail())) {
            loginService.changePassword(passwordChangeRequest);
            loginService.removePasswordChangeTtl(passwordChangeRequest.getEmail());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            throw new BadRequest();
        }
    }

}
