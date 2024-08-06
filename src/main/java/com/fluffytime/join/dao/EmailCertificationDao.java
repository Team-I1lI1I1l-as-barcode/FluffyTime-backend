package com.fluffytime.join.dao;

import com.fluffytime.join.dto.TempUser;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailCertificationDao {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveEmailCertificationTempUser(TempUser user) {
        redisTemplate.opsForValue().set(user.getEmail(), user, Duration.ofSeconds(300));
    }

    public Optional<TempUser> getTempUser(String email) {
        TempUser tempUser = (TempUser) redisTemplate.opsForValue().get(email);
        return Optional.ofNullable(tempUser);
    }


    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(email));
    }

    public void removeTempUser(String email) {
        redisTemplate.delete(email);
    }
}
