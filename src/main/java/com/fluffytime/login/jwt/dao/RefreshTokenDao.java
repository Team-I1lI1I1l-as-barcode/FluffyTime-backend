package com.fluffytime.login.jwt.dao;

import static com.fluffytime.login.jwt.util.JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenDao {

    public static final String keyHeader = "refresh_token:";

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(keyHeader + email, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_COUNT/1000));
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(keyHeader + email);
    }


    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(keyHeader + email));
    }

    public void removeRefreshToken(String email) {
        redisTemplate.delete(keyHeader + email);
    }
}
