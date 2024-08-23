package com.fluffytime.user.service;

import com.fluffytime.auth.jwt.dao.RefreshTokenDao;
import com.fluffytime.auth.jwt.exception.TokenNotFound;
import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.common.exception.global.UserNotFound;
import com.fluffytime.domain.User;
import com.fluffytime.repository.UserRepository;
import com.fluffytime.user.dto.request.LoginUser;
import com.fluffytime.user.exception.MismatchedPassword;
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
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final JwtTokenizer jwtTokenizer;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenDao refreshTokenDao;

    public void loginProcess(HttpServletResponse response, LoginUser loginUser) {

        User user = userRepository.findByEmail(loginUser.getEmail()).orElseThrow(UserNotFound::new);

        if(!passwordEncoder.matches(loginUser.getPassword(),user.getPassword())) {
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

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000)); // 7일

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000)); // 7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        log.info("로그인에 성공하였습니다.");
    }

    public ResponseEntity<Void> logoutProcess(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        String refreshToken = jwtTokenizer.getTokenFromCookie(request, "refreshToken");

        log.info("refreshToken={}", refreshToken);

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
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");

        //Access 토큰 Cookie 값 0
        Cookie accessTokencookie = new Cookie("accessToken", null);
        accessTokencookie.setMaxAge(0);
        accessTokencookie.setPath("/");

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokencookie);
        response.sendRedirect("/login");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
