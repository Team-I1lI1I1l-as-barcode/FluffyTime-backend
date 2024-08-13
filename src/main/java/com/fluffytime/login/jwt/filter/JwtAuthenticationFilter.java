package com.fluffytime.login.jwt.filter;

import static com.fluffytime.login.jwt.util.JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT;

import com.fluffytime.login.jwt.dao.RefreshTokenDao;
import com.fluffytime.login.jwt.exception.JwtErrorCode;
import com.fluffytime.login.jwt.util.JwtResponseProvider;
import com.fluffytime.login.jwt.util.JwtTokenizer;
import com.fluffytime.login.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RefreshTokenDao refreshTokenDao;
    private final JwtTokenizer jwtTokenizer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getToken(request, "accessToken");
        String refreshToken = getToken(request, "refreshToken");

        log.info("accessToken = {}", accessToken);
        log.info("refreshToken = {}", refreshToken);

        if (StringUtils.hasText(refreshToken) && !StringUtils.hasText(accessToken)) {
            // 리프레쉬 토큰 검증
            Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
            Long userId = Long.valueOf((Integer) claims.get("userId"));
            String email = claims.getSubject();
            String nickname = (String) claims.get("nickname");
            List roles = (List) claims.get("roles");

            String getRefreshToken = refreshTokenDao.getRefreshToken(email);
            if (getRefreshToken != null) {
                boolean isSame = getRefreshToken.equals(refreshToken);
                if(isSame) {
                    String newAccessToken = jwtTokenizer.createAccessToken(userId, email, nickname,
                        roles);
                    doAuthentication(request, newAccessToken);
                    Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setHttpOnly(true); // 클라이언트 스크립트에서 쿠키 접근 방지
                    accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT) / 1000); // 만료 시간 설정 (예: 1시간)
                    response.addCookie(accessTokenCookie);
                } else {
                    refreshTokenDao.removeRefreshToken(email);
                    Cookie refreshTokenCookie = new Cookie("refreshToken", null);
                    refreshTokenCookie.setPath("/");
                    refreshTokenCookie.setMaxAge(0); // 만료 시간 설정 (예: 1시간)
                    response.addCookie(refreshTokenCookie);
                }
            }
        }

        if (StringUtils.hasText(accessToken)) {
            doAuthentication(request, accessToken);
        }
        filterChain.doFilter(request, response);
    }

    private void doAuthentication(HttpServletRequest request ,String token) {
        try {
            // accessToken을 사용하여 사용자 인증 정보를 가져오는 메서드를 호출
            getAuthentication(token);
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", "EXPIRED_TOKEN");
            log.error("Expired Token : {}", token, e);
            throw new BadCredentialsException("Expried token exception", e);
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", "UNSUPPORTED_TOKEN");
            log.error("Unsupported Token: {}", token, e);
            throw new BadCredentialsException("Unsupported token exception", e);
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", "INVALID_TOKEN");
            log.error("Invalid Token: {}", token, e);
            throw new BadCredentialsException("Invalid token exception", e);
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", "NOT_FOUND_TOKEN");
            log.error("Token not found: {}", token, e);
            throw new BadCredentialsException("Token not found exception", e);
        } catch (Exception e) {
            log.error("JWT Filter - Internal Error: {}", token, e);
            throw new BadCredentialsException("JWT filter internal exception", e);
        }
    }


    // HTTP 요청에서 JWT 토큰 추출하기
    private String getToken(HttpServletRequest request, String targetTokenName) {
        // HTTP 요청 헤더에서 Authorization(권한) 값 추출
        String authorization = request.getHeader("Authorization");

        // 서버로 인증 토큰 전달하는 방법 1
        // Authorization 헤더가 존재하고, "Bearer "로 시작하는 경우
        // 즉 클라이언트가 Bearer 스키마를 사용하여 인증 토큰을 전송하고 있을 경우
        // 인증 토큰 값과의 구분이 필요하므로 Bearer 뒤에 공백 하나 두기
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            // "Bearer " 부분을 제외한 토큰 값을 반환
            return authorization.substring(7);
        }

        // 서버로 인증 토큰 전달하는 방법 2 -> 쿠키로 전송하고 있는 경우
        // HTTP 요청 쿠키에서 accessToken 쿠키 값 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (targetTokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 만약 헤더나 쿠키에서 토큰 값을 찾지 못한 경우 null을 반환
        return null;
    }


    // getToken()을 통해 사용자의 인증 정보를 가져오고,
    // JWT에서 추출한 Claim으로 getGrantedAuthorities()로 권한을 가져와 SecurityContextHolder 설정
    private void getAuthentication(String token) {
        // JWT 토큰을 파싱하여 클레임(claim)들을 추출합니다.
        Claims claims = jwtTokenizer.parseAccessToken(token);

        // 클레임에서 사용자 정보 추출
        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String nickname = claims.get("nickname", String.class);

        // JWT에서 추출한 Claim으로 토큰에 권한 추출
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        // CustomUserDetails 객체 생성
        CustomUserDetails userDetails = new CustomUserDetails(userId, email, "", nickname,
            authorities.stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList()));
        // Authentication 생성 (권한 목록, 사용자 정보, 인증여부)
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Claims 객체에서 사용자의 권한 추출하여 리스트로 반환
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        // 클레임에서 "roles" 정보를 추출(사용자가 가지고 있는 권한 목록)
        List<String> roles = (List<String>) claims.get("roles");
        // 권한 정보를 담을 리스트를 생성
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 각 역할(role)을 GrantedAuthority 객체로 변환하여 리스트에 추가
        for (String role : roles) {
            authorities.add(() -> role);
        }
        // 권한 목록을 반환
        return authorities;
    }
}
