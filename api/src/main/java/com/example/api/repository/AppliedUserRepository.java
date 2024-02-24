package com.example.api.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * redis set 자료구조를 이용해서 사용자 1명당 1개의 쿠폰만 사용가능하게 적용하기
 */
@Repository
public class AppliedUserRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public AppliedUserRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long add(Long userId) {
        return redisTemplate
                .opsForSet()
                .add("applied_user", String.valueOf(userId));
    }

}
