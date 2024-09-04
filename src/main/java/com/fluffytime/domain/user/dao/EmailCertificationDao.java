package com.fluffytime.domain.user.dao;

import com.fluffytime.domain.user.dto.redis.TempUser;
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

    // 이메일 인증에 들어간 회원가입 유저의 정보를 임시 저장하는 메서드
    public void saveEmailCertificationTempUser(TempUser user) {
        redisTemplate.opsForValue().set(
            EMAIL_CERTIFICATION_KEY_HEADER + user.getEmail(),
            user,
            Duration.ofSeconds(EMAIL_CERTIFICATION_EXPIRY)
        );
    }

    // 임지 저장된 유저정보를 가져오는 메서드
    public Optional<TempUser> getTempUser(String email) {
        TempUser tempUser = (TempUser) redisTemplate.opsForValue().get(EMAIL_CERTIFICATION_KEY_HEADER + email);
        return Optional.ofNullable(tempUser);
    }

    // 임시 저장된 유저 정보를 제거하는 메서드
    public void removeTempUser(String email) {
        redisTemplate.delete(EMAIL_CERTIFICATION_KEY_HEADER + email);
    }
}
