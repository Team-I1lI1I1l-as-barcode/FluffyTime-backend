package com.fluffytime.global.auth.jwt.filter;

import static com.fluffytime.global.auth.jwt.util.constants.TokenClaimsKey.NICKNAME;
import static com.fluffytime.global.auth.jwt.util.constants.TokenClaimsKey.ROLES;
import static com.fluffytime.global.auth.jwt.util.constants.TokenClaimsKey.USER_ID;

import com.fluffytime.global.auth.jwt.token.JwtAuthenticationToken;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.auth.jwt.dto.CustomUserDetails;
import com.fluffytime.global.auth.jwt.util.constants.TokenName;
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
import java.util.Collection;
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
        String accessToken = jwtTokenizer.getTokenFromCookie(request, TokenName.ACCESS_TOKEN_NAME.getName());

        if (StringUtils.hasText(accessToken)) {
            doAuthentication(request, accessToken);
        }

        filterChain.doFilter(request, response);
    }

    // token 값을 가지고 인증 절차 진행
    private void doAuthentication(HttpServletRequest request ,String token) {
        try {
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

        Claims claims = jwtTokenizer.parseAccessToken(token);

        String email = claims.getSubject();
        Long userId = claims.get(USER_ID.getKey(), Long.class);
        String nickname = claims.get(NICKNAME.getKey(), String.class);

        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        CustomUserDetails userDetails = new CustomUserDetails(userId, email, "", nickname,
            authorities.stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList()));

        Authentication authentication = new JwtAuthenticationToken(authorities,userDetails,null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Claims 객체에서 사용자의 권한 추출하여 리스트로 반환
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get(ROLES.getKey());
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }
}
