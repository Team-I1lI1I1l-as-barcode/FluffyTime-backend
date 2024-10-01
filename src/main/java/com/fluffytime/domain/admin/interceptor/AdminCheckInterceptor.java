package com.fluffytime.domain.admin.interceptor;

import static com.fluffytime.global.auth.jwt.util.constants.TokenClaimsKey.ROLES;
import static com.fluffytime.global.auth.jwt.util.constants.TokenName.ACCESS_TOKEN_NAME;

import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

// 역할 확인 정보 반환 인터셉터 (admin 역할 확인)
@Slf4j
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {

    private final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    private final String IS_ADMIN_HEADER = "Is-Admin";

    private final JwtTokenizer jwtTokenizer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String accessToken = jwtTokenizer.getTokenFromCookie(request, ACCESS_TOKEN_NAME.getName());

        if (accessToken == null) {
            return true;
        }

        Claims claims = jwtTokenizer.parseAccessToken(accessToken);

        List<String> roles = claims.get(ROLES.getKey(), List.class); // 권한 정보 추출

        if (roles != null && roles.contains(ADMIN_ROLE_NAME)) {

            // 응답 헤더에 ROLE_ADMIN 여부를 추가
            response.addHeader(IS_ADMIN_HEADER, "true");
            return true; // ROLE_ADMIN 권한이 있을 경우 요청을 계속 진행
        }

        response.addHeader(IS_ADMIN_HEADER, "false");
        return true; // ROLE_ADMIN 권한이 있을 경우 요청을 계속 진행
    }
}
