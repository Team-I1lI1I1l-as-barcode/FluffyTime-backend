package com.fluffytime.domain.user.service;

import static com.fluffytime.global.auth.jwt.util.TokenCookieManager.generateTokenCookie;
import static com.fluffytime.global.auth.jwt.util.TokenCookieManager.removeCookie;
import static com.fluffytime.global.auth.jwt.util.constants.TokenExpiry.ACCESS_TOKEN_EXPIRY_SECOND;
import static com.fluffytime.global.auth.jwt.util.constants.TokenExpiry.REFRESH_TOKEN_EXPIRY_SECOND;
import static com.fluffytime.global.auth.jwt.util.constants.TokenName.ACCESS_TOKEN_NAME;
import static com.fluffytime.global.auth.jwt.util.constants.TokenName.REFRESH_TOKEN_NAME;

import com.fluffytime.domain.user.dao.PasswordChangeDao;
import com.fluffytime.domain.user.dto.request.FindEmailRequest;
import com.fluffytime.domain.user.dto.request.LoginUserRequest;
import com.fluffytime.domain.user.dto.request.PasswordChangeRequest;
import com.fluffytime.domain.user.dto.response.FindEmailResponse;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.exception.MismatchedPassword;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.global.auth.jwt.dao.RefreshTokenDao;
import com.fluffytime.global.auth.jwt.exception.TokenNotFound;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.common.exception.global.UserNotFound;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenDao refreshTokenDao;
    private final PasswordChangeDao passwordChangeDao;

    // 로그인 서비스
    @Transactional
    public void loginProcess(HttpServletResponse response, LoginUserRequest loginUser) {

        User user = userRepository.findByEmail(loginUser.getEmail()).orElseThrow(UserNotFound::new);

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new MismatchedPassword();
        }

        Long userId = user.getUserId();
        String email = user.getEmail();
        String nickname = user.getNickname();
        List<String> roles = user.getUserRoles().stream().map(userRole -> userRole
            .getRole()
            .getRoleName()
            .getName()
        ).toList();

        //토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(userId, email, nickname, roles);
        String refreshToken = jwtTokenizer.createRefreshToken(userId, email, nickname, roles);

        refreshTokenDao.saveRefreshToken(email, refreshToken);

        Cookie accessTokenCookie = generateTokenCookie(
            ACCESS_TOKEN_NAME.getName(),
            accessToken,
            ACCESS_TOKEN_EXPIRY_SECOND.getExpiry()
        );

        Cookie refreshTokenCookie = generateTokenCookie(
            REFRESH_TOKEN_NAME.getName(),
            refreshToken,
            REFRESH_TOKEN_EXPIRY_SECOND.getExpiry()
        );

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        log.info("로그인에 성공하였습니다.");
    }

    // 로그아웃 서비스
    @Transactional
    public ResponseEntity<Void> logoutProcess(HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {

        String refreshToken = jwtTokenizer.getTokenFromCookie(request,
            REFRESH_TOKEN_NAME.getName());

        if (refreshToken == null) {
            throw new TokenNotFound();
        }

        //DB에 저장되어 있는지 확인
        String email = jwtTokenizer.getEmailFromRefreshToken(refreshToken);
        String checkRefreshToken = refreshTokenDao.getRefreshToken(email);

        if (!StringUtils.hasText(checkRefreshToken)) {
            throw new TokenNotFound();
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshTokenDao.removeRefreshToken(email);

        //Refresh 토큰 Cookie 값 0
        Cookie refreshTokenCookie = removeCookie(REFRESH_TOKEN_NAME.getName());

        //Access 토큰 Cookie 값 0
        Cookie accessTokencookie = removeCookie(ACCESS_TOKEN_NAME.getName());

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokencookie);
        response.sendRedirect("/login");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 이메일 찾기 서비스
    @Transactional
    public ResponseEntity<FindEmailResponse> findEmail(FindEmailRequest findEmailRequest) {
        FindEmailResponse findEmailResponse = FindEmailResponse.builder()
            .email(findEmailRequest.getEmail())
            .isExists(userRepository.existsUserByEmail(findEmailRequest.getEmail()))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(findEmailResponse);
    }

    // email로 가입된 유저가 존재하는지 확인
    @Transactional
    public boolean existsUserByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    // 비밀번호 변경 ttl 저장
    @Transactional
    public void savePasswordChangeTtl(String email) {
        passwordChangeDao.saveChangePasswordTtl(email);
    }

    // 비밀번호 변경 ttl key 존재 여부 확인
    @Transactional
    public boolean findPasswordChangeTtl(String email) {
        return passwordChangeDao.hasKey(email);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        String email = passwordChangeRequest.getEmail();
        String password = passwordChangeRequest.getPassword();

        User findUser = userRepository.findByEmail(email).orElseThrow(UserNotFound::new);

        findUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(findUser);
    }

    // 비밀번호 변경 ttl 제거
    @Transactional
    public void removePasswordChangeTtl(String email) {
        passwordChangeDao.removePasswordChangeTtl(email);
    }
}
