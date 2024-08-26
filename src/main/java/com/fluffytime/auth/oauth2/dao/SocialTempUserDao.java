package com.fluffytime.auth.oauth2.dao;

import com.fluffytime.auth.oauth2.dto.SocialTempUser;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SocialTempUserDao {

    public static final String keyHeader = "social_temp:";

    private final RedisTemplate<String, Object> redisTemplate;

    // refresh token redis 저장
    public void saveSocialTempUser(String email, SocialTempUser tempUser) {
        redisTemplate.opsForValue().set(keyHeader + email, tempUser,Duration.ofSeconds(60*60*24));
    }

    // refresh token key 값으로 불러오기
    public Optional<SocialTempUser> getSocialTempUser(String email) {
        SocialTempUser socialTempUser = (SocialTempUser) redisTemplate.opsForValue()
            .get(keyHeader + email);
        return Optional.ofNullable(socialTempUser);
    }

    // refresh token key 값으로 삭제
    public void removeSocialTempUser(String email) {
        redisTemplate.delete(keyHeader + email);
    }

    public String getKey(String email) {
        return keyHeader + email;
    }
}
