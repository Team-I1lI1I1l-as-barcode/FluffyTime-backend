package com.fluffytime.join.controller.api;

import com.fluffytime.join.dto.request.JoinRequest;
import com.fluffytime.join.dto.response.CheckDuplicationResponse;
import com.fluffytime.join.dto.response.JoinResponse;
import com.fluffytime.join.dto.response.SucceedSendEmailResponse;
import com.fluffytime.join.dto.response.SucceedCertificationResponse;
import com.fluffytime.join.service.CertificationService;
import com.fluffytime.join.service.JoinService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class joinApiController {

    private final JoinService joinService;
    private final CertificationService certificationService;

    @PostMapping("/temp-join")
    public ResponseEntity<JoinResponse> tempJoin(
        @RequestBody @Valid JoinRequest joinUser
    ) {
        return ResponseEntity.created(URI.create("/join/email-certificate/" + joinUser.getEmail()))
            .body(joinService.tempJoin(joinUser));
    }

    @GetMapping("/join")
    public ResponseEntity<JoinResponse> join(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(joinService.join(email));
    }

    @GetMapping("/check-email")
    public ResponseEntity<CheckDuplicationResponse> checkEmail(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(joinService.checkExistsEmail(email));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<CheckDuplicationResponse> checkNickname(
        @RequestParam(name = "nickname")
        @NotBlank
        String nickname
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(joinService.checkExistsNickname(nickname));
    }

    @GetMapping("/email-certification/send")
    public ResponseEntity<SucceedSendEmailResponse> sendCertification(
        @RequestParam(name = "email")
        @NotBlank
        @Email
        String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(certificationService.sendCertificationMail(email));
    }

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
