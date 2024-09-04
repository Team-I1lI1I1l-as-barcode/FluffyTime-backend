package com.fluffytime.domain.user.dao;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBlockDao {

    private final RedisTemplate<String, String> redisTemplate;

    // SET 자료 구조로 차단 리스트 관리 -> 중복 불 허용
    // 차단하는 유저 - 차단 당하는 유저 저장
    public void saveUserBlockList(String blocker, String targetUser) {
        redisTemplate.opsForSet().add(blocker, targetUser);
    }

    // 차단된 유저 목록 조회
    public Set<String> getUserBlockList(String blocker) {
        return redisTemplate.opsForSet().members(blocker);
    }

    // 특정 유저 차단 해제
    public void removeUserBlockList(String blocker, String targetUser) {
        redisTemplate.opsForSet().remove(blocker, targetUser);
    }
}

