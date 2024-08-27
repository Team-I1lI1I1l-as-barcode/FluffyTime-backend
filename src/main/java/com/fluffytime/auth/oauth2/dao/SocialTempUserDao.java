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

    public static final String SOCIAL_TEMP_USER_KEY_HEADER = "social_temp:";
    public static final int SOCIAL_TEMP_USER_EXPIRY = 60*60*24;

    private final RedisTemplate<String, Object> redisTemplate;

    // refresh token redis 저장
    public void saveSocialTempUser(String email, SocialTempUser tempUser) {
        redisTemplate.opsForValue().set(
            SOCIAL_TEMP_USER_KEY_HEADER + email,
            tempUser,
            Duration.ofSeconds(SOCIAL_TEMP_USER_EXPIRY)
        );
    }

    // refresh token key 값으로 불러오기
    public Optional<SocialTempUser> getSocialTempUser(String email) {
        SocialTempUser socialTempUser = (SocialTempUser) redisTemplate.opsForValue()
            .get(SOCIAL_TEMP_USER_KEY_HEADER + email);
        return Optional.ofNullable(socialTempUser);
    }

    // refresh token key 값으로 삭제
    public void removeSocialTempUser(String email) {
        redisTemplate.delete(SOCIAL_TEMP_USER_KEY_HEADER + email);
    }

    public String getKey(String email) {
        return SOCIAL_TEMP_USER_KEY_HEADER + email;
    }
}
