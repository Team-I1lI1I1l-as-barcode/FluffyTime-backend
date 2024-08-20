package com.fluffytime.auth.jwt.filter;

import com.fluffytime.auth.jwt.token.JwtAuthenticationToken;
import com.fluffytime.auth.jwt.util.JwtTokenizer;
import com.fluffytime.auth.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;

    // access token 검증
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenizer.getTokenFromCookie(request, "accessToken");

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
        Authentication authentication = new JwtAuthenticationToken(authorities,userDetails,null);


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
