package com.fluffytime.login.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenizer {

    // accessToken에 사용될 비밀키를 담을 배열 선언
    private final byte[] accessSecret;
    // refreshToken에 사용될 비밀 키를 담을 배열 선언
    private final byte[] refreshSecret;
    // accessToken 유지 시간(ms)
    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30분
    // refreshToken 유지 시간(ms)
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7일

    // 토큰 초기와 - application.yml 에서 key값 가져오기
    public JwtTokenizer(@Value("${jwt.refreshKey}") String accessSecret,
        @Value("${jwt.refreshKey}") String refreshSecret) {
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    /*
     * AccessToken 생성
     * */
    public String createAccessToken(Long id, String email, String nickname, List<String> roles) {
        return createToken(id, email, nickname, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }

    /*
     * RefreshToken 생성
     * */
    public String createRefreshToken(Long id, String email, String nickname, List<String> roles) {
        return createToken(id, email, nickname, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }

    // 토큰 생성 - 구분 id, 이메일, 유저명, 사용자 권한들, 유효기간, 시크릿키
    private String createToken(Long id, String email, String nickname,
        List<String> roles, Long expire, byte[] secretKey) {
        // 기본적으로 가지고 있는 claim : subject
        Claims claims = Jwts.claims().setSubject(email);

        claims.put("userId", id);
        claims.put("nickname", nickname);
        claims.put("roles", roles);

        // JWT 생성
        return Jwts.builder()
            .setClaims(claims) // JWT의 클레임을 설정
            .setIssuedAt(new Date()) // 토큰 발급 시각
            .setExpiration(new Date(new Date().getTime() + expire)) // 토큰 만료 시각
            .signWith(getSigningKey(secretKey)) // JWT에 비밀키를 사용하여 서명 추가
            .compact(); // JWT를 생성하고, 문자열 형태로 변환하여 반환
    }

    /*
     * 토큰에서 유저 메일 얻기
     * */
    public Long getUserIdFromToken(String token) {
        // 토큰을 공백 기준으로 분리하여 실제 토큰 값만 추출
        String[] tokenArr = token.split(" ");
        token = tokenArr[1];
        // 토큰을 파싱하여 클레임 객체를 얻고, 해당 클레임에서 userId를 Long형으로 반환
        Claims claims = parseToken(token, accessSecret);
        return Long.valueOf((Integer) claims.get("userId"));
    }

    // 액세스 토큰을 파싱하여 클레임 객체를 반환하는 메서드
    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    // 리프레쉬 토큰을 파싱하여 클레임 객체를 반환하는 메서드
    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    // 주어진 토큰을 파싱하여 클레임 객체를 반환하는 메서드
    public Claims parseToken(String token, byte[] secretKey) {
        // 주어진 비밀키로 서명키를 설정한 후 토큰을 파싱하여 클레임을 반환
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey(secretKey))
            .build()
            .parseClaimsJws(token).
            getBody();
    }

    /*
     * @Param secretKey - byte 형식
     * @Return Key 형식 시크릿 키
     * 실제 애플리케이션에서 사용할 안전한 비밀키를 이용하여, 서명키를 얻는다.
     * 해당 서명 키를 이용해 JWT를 서명하거나 검증한다.
     * HMAC-SHA256 알고리즘을 사용하여 랜덤하게 생성된 안전한 비밀키를 반환
     * */
    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }
}
