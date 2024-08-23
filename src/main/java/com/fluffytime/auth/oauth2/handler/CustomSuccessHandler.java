package com.fluffytime.auth.oauth2.handler;

import com.fluffytime.auth.jwt.dao.RefreshTokenDao;
import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.auth.oauth2.dao.SocialTempUserDao;
import com.fluffytime.auth.oauth2.dto.CustomOAuth2User;
import com.fluffytime.auth.oauth2.dto.SocialTempUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final SocialTempUserDao socialTempUserDao;
    private final RefreshTokenDao refreshTokenDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = customOAuth2User.getUserId();
        String email = customOAuth2User.getName();
        String nickname = customOAuth2User.getNickname();
        List<String> roles = customOAuth2User.getRoles();

        SocialTempUser socialTempUser = socialTempUserDao.getSocialTempUser(email).orElse(null);

        if (socialTempUser == null) {

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

            response.sendRedirect("/");
        } else {
            // 추가 정보 기입 페이지로 이동
            log.info("간편 회원가입 화면으로 이동");
            response.sendRedirect("/join/social?email="+email);
        }


    }
}
