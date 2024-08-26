package com.fluffytime.user.dao;

import com.fluffytime.user.dto.TempUser;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailCertificationDao {

    private static final String EMAIL_CERTIFICATION_KEY_HEADER = "email_certification:";
    private static final int EMAIL_CERTIFICATION_EXPIRY = 300;

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveEmailCertificationTempUser(TempUser user) {
        redisTemplate.opsForValue().set(
            EMAIL_CERTIFICATION_KEY_HEADER + user.getEmail(),
            user,
            Duration.ofSeconds(EMAIL_CERTIFICATION_EXPIRY)
        );
    }

    public Optional<TempUser> getTempUser(String email) {
        TempUser tempUser = (TempUser) redisTemplate.opsForValue().get(EMAIL_CERTIFICATION_KEY_HEADER + email);
        return Optional.ofNullable(tempUser);
    }


    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(EMAIL_CERTIFICATION_KEY_HEADER + email));
    }

    public void removeTempUser(String email) {
        redisTemplate.delete(EMAIL_CERTIFICATION_KEY_HEADER + email);
    }
}
