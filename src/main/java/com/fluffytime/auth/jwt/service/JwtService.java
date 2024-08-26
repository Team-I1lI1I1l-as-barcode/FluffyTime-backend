package com.fluffytime.auth.jwt.service;

import com.fluffytime.auth.jwt.dao.RefreshTokenDao;
import com.fluffytime.auth.jwt.exception.InvalidToken;
import com.fluffytime.auth.jwt.exception.TokenNotFound;
import com.fluffytime.auth.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenDao refreshTokenDao;

    // refresh token rotation & access token 재발급
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenizer.getTokenFromCookie(request, "refreshToken");

        if (refreshToken == null) {
            throw new TokenNotFound();
        }

        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);

        Long userId = Long.valueOf((Integer) claims.get("userId"));
        String email = claims.getSubject();
        String nickname = (String) claims.get("nickname");
        List roles = (List) claims.get("roles");


        String getRefreshToken = refreshTokenDao.getRefreshToken(email);

        if(getRefreshToken == null) {
            throw new TokenNotFound();
        }

        boolean isSame = getRefreshToken.equals(refreshToken);

        if (isSame) {
            String newAccessToken = jwtTokenizer.createAccessToken(userId, email, nickname,
                roles);
            Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT)
                / 1000);

            response.addCookie(accessTokenCookie);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            refreshTokenDao.removeRefreshToken(email);

            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");

            Cookie accessTokencookie = new Cookie("accessToken", null);
            accessTokencookie.setMaxAge(0);
            accessTokencookie.setPath("/");

            response.addCookie(refreshTokenCookie);
            response.addCookie(accessTokencookie);

            throw new InvalidToken();
        }
    }
}