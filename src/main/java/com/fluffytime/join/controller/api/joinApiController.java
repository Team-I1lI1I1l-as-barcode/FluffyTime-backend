package com.fluffytime.join.controller.api;

import com.fluffytime.join.dto.request.JoinRequest;
import com.fluffytime.join.dto.response.ApiResponse;
import com.fluffytime.join.dto.response.JoinResponse;
import com.fluffytime.join.dto.response.SendEmailResponse;
import com.fluffytime.join.dto.response.SucceedCertificationResponse;
import com.fluffytime.join.service.CertificationService;
import com.fluffytime.join.service.JoinService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class joinApiController {

    private final JoinService joinService;
    private final CertificationService certificationService;

    @PostMapping("/temp-join")
    public ResponseEntity<ApiResponse<JoinResponse>> tempJoin(
        @RequestBody @Valid JoinRequest joinUser
    ) {
        return ResponseEntity.ok(joinService.tempJoin(joinUser));
    }

    @GetMapping("/join")
    public ResponseEntity<ApiResponse<JoinResponse>> join(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.ok(joinService.join(email));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Void>> checkEmail(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.ok(joinService.checkExistsEmail(email));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Void>> checkNickname(
        @RequestParam(name = "nickname")
        @NotBlank
        String nickname
    ) {
        return ResponseEntity.ok(joinService.checkExistsNickname(nickname));
    }

    @GetMapping("/email-certification/send")
    public ResponseEntity<ApiResponse<SendEmailResponse>> sendCertification(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.ok(certificationService.sendCertificationMail(email));
    }

    @GetMapping("/email-certification")
    public ResponseEntity<ApiResponse<SucceedCertificationResponse>> certificateEmail(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.ok(certificationService.certificateEmail(email));
    }
}
