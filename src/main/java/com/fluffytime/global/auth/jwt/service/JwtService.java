package com.fluffytime.global.auth.jwt.service;

import static com.fluffytime.global.auth.jwt.util.TokenCookieManager.generateTokenCookie;
import static com.fluffytime.global.auth.jwt.util.TokenCookieManager.removeCookie;
import static com.fluffytime.global.auth.jwt.util.constants.TokenClaimsKey.NICKNAME;
import static com.fluffytime.global.auth.jwt.util.constants.TokenClaimsKey.ROLES;
import static com.fluffytime.global.auth.jwt.util.constants.TokenClaimsKey.USER_ID;
import static com.fluffytime.global.auth.jwt.util.constants.TokenName.ACCESS_TOKEN_NAME;
import static com.fluffytime.global.auth.jwt.util.constants.TokenName.REFRESH_TOKEN_NAME;

import com.fluffytime.global.auth.jwt.dao.RefreshTokenDao;
import com.fluffytime.global.auth.jwt.exception.InvalidToken;
import com.fluffytime.global.auth.jwt.exception.TokenNotFound;
import com.fluffytime.global.auth.jwt.util.JwtTokenizer;
import com.fluffytime.global.auth.jwt.util.constants.TokenExpiry;
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
        String refreshToken = jwtTokenizer.getTokenFromCookie(request, REFRESH_TOKEN_NAME.getName());

        if (refreshToken == null) {
            throw new TokenNotFound();
        }

        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);

        Long userId = Long.valueOf((Integer) claims.get(USER_ID.getKey()));
        String email = claims.getSubject();
        String nickname = (String) claims.get(NICKNAME.getKey());
        List roles = (List) claims.get(ROLES.getKey());


        String getRefreshToken = refreshTokenDao.getRefreshToken(email);

        if(getRefreshToken == null) {
            throw new TokenNotFound();
        }

        boolean isSame = getRefreshToken.equals(refreshToken);

        if (isSame) {
            String newAccessToken = jwtTokenizer.createAccessToken(userId, email, nickname,
                roles);

            Cookie accessTokenCookie = generateTokenCookie(
                ACCESS_TOKEN_NAME.getName(),
                newAccessToken,
                TokenExpiry.ACCESS_TOKEN_EXPIRY_SECOND.getExpiry()
            );

            response.addCookie(accessTokenCookie);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            refreshTokenDao.removeRefreshToken(email);

            Cookie refreshTokenCookie = removeCookie(REFRESH_TOKEN_NAME.getName());

            Cookie accessTokencookie = removeCookie(ACCESS_TOKEN_NAME.getName());

            response.addCookie(refreshTokenCookie);
            response.addCookie(accessTokencookie);

            throw new InvalidToken();
        }
    }
}