package com.fluffytime.login.jwt.filter;

import com.fluffytime.login.jwt.dao.RefreshTokenDao;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenDao refreshTokenDao;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("/api/users/logout")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refresh = cookie.getValue();
            }
        }

        //refresh null check
        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        String email = jwtTokenizer.getEmailFromRefreshToken(refresh);
        String checkRefreshToken = refreshTokenDao.getRefreshToken(email);

        if (!StringUtils.hasText(checkRefreshToken)) {
            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshTokenDao.removeRefreshToken(email);

        //Refresh 토큰 Cookie 값 0
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");

        //Refresh 토큰 Cookie 값 0
        Cookie accessTokencookie = new Cookie("accessToken", null);
        accessTokencookie.setMaxAge(0);
        accessTokencookie.setPath("/");

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokencookie);
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("User logout = {}", email);
    }
}
