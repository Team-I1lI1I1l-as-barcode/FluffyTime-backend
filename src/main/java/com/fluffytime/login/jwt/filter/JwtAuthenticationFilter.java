package com.fluffytime.login.jwt.filter;

import com.fluffytime.login.exception.ExpiredToken;
import com.fluffytime.login.exception.InvalidToken;
import com.fluffytime.login.exception.JwtFilterError;
import com.fluffytime.login.exception.NotFoundToken;
import com.fluffytime.login.exception.UnsupportedToken;
import com.fluffytime.login.jwt.token.JwtAuthenticationToken;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (StringUtils.hasText(token)) {
            try {
                getAuthentication(token);
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", "EXPIRED_TOKEN");
                log.error("Expired Token : {}", token, e);
//                throw new BadCredentialsException("Expried token exception", e);
                throw new ExpiredToken();
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", "UNSUPPORTED_TOKEN");
                log.error("Unsupported Token: {}", token, e);
//                throw new BadCredentialsException("Unsupported token exception", e);
                throw new UnsupportedToken();
            } catch (MalformedJwtException e) {
                request.setAttribute("exception", "INVALID_TOKEN");
                log.error("Invalid Token: {}", token, e);
//                throw new BadCredentialsException("Invalid token exception", e);
                throw new InvalidToken();
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", "NOT_FOUND_TOKEN");
                log.error("Token not found: {}", token, e);
//                throw new BadCredentialsException("Token not found exception", e);
                throw new NotFoundToken();
            } catch (Exception e) {
                log.error("JWT Filter - Internal Error: {}", token, e);
//                throw new BadCredentialsException("JWT filter internal exception", e);
                throw new JwtFilterError();
            }
        }
        filterChain.doFilter(request, response);
    }

    private void getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        String email = claims.getSubject();
//        Long userId = claims.get("userId", Long.class);
        String nickname = claims.get("nickname", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        CustomUserDetails userDetails = new CustomUserDetails(email, "", nickname,
            authorities.stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList()));
        Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
