package com.fluffytime.login.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluffytime.login.dto.request.LoginUser;
import com.fluffytime.login.jwt.dao.RefreshTokenDao;
import com.fluffytime.login.jwt.exception.JwtErrorCode;
import com.fluffytime.login.jwt.util.JwtResponseProvider;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.login.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenDao refreshTokenDao;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        try {
            // ObjectMapper를 사용하여 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            LoginUser LoginUser = objectMapper.readValue(request.getInputStream(), LoginUser.class);

            // 로그인 인증 정보 추출
            String email = LoginUser.getEmail();
            String password = LoginUser.getPassword();

            // Authentication 객체 생성 후 인증 시도
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password);
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new AuthenticationException("Error reading JSON request", e) {};
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authentication)
        throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Long id = customUserDetails.getId();
        String email = customUserDetails.getUsername();
        String nickname = customUserDetails.getNickname();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        String accessToken = jwtTokenizer.createAccessToken(id,email,nickname,roles);
        String refreshToken = jwtTokenizer.createRefreshToken(id,email,nickname,roles);

        refreshTokenDao.saveRefreshToken(email, refreshToken);

        // 쿠키에 토큰 저장
        // 엑세스 토큰 쿠키
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT) / 1000); // 30분

        // 리프레시 토큰 쿠키
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(
            Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000)); // 7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.sendRedirect("/");

        JwtResponseProvider.setResponse(response, JwtErrorCode.SUCCESS);
        log.info("로그인에 성공하였습니다.");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        JwtResponseProvider.setResponse(response, JwtErrorCode.FAIL_LOGIN);
    }
}
