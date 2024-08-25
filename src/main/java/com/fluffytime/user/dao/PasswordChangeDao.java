package com.fluffytime.user.dao;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PasswordChangeDao {

    private static final String keyHeader = "change_password:";

    private final RedisTemplate<String, String> redisTemplate;

    public void saveChangePasswordTtl(String email) {
        redisTemplate.opsForValue().set(keyHeader + email, email, Duration.ofSeconds(300));
    }

    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(keyHeader + email));
    }

    public void removePasswordChangeTtl(String email) {
        redisTemplate.delete(keyHeader + email);
    }
}
