package com.fluffytime.auth.jwt.dao;

import static com.fluffytime.auth.jwt.util.JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenDao {

    public static final String keyHeader = "refresh_token:";

    private final RedisTemplate<String, String> redisTemplate;

    // refresh token redis 저장
    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(keyHeader + email, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_COUNT/1000));
    }

    // refresh token key 값으로 불러오기
    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(keyHeader + email);
    }

    // refresh token key 값으로 삭제
    public void removeRefreshToken(String email) {
        redisTemplate.delete(keyHeader + email);
    }
}
