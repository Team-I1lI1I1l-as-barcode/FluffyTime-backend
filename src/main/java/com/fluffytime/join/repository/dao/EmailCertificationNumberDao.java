package com.fluffytime.join.repository.dao;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailCertificationNumberDao {

    private final StringRedisTemplate stringRedisTemplate;

    public void saveCertificationNumber(String email, String certificationNumber) {
        stringRedisTemplate.opsForValue()
            .set(email, certificationNumber, Duration.ofSeconds(60));
    }

    public String getCertificationNumber(String email) {
        return stringRedisTemplate.opsForValue().get(email);
    }

    public void removeCertificationNumber(String email) {
        stringRedisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(email));
    }

}
