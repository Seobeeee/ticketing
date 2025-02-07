package com.example.cache.domain.service;

import com.example.cache.domain.entity.RedisHashUser;
import com.example.cache.domain.entity.User;
import com.example.cache.domain.repository.RedisHashUserRepository;
import com.example.cache.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.example.cache.config.CacheConfig.CACHE1;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisHashUserRepository redisHashUserRepository;
    private final RedisTemplate<String, User> userRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    public User getUser(final Long id){
        var key = "users:%d".formatted(id);
        // 캐시 확인
        var user = objectRedisTemplate.opsForValue().get(key);
        if (user != null){
            return (User)user;
        }

        // DB 조회 후 캐시 저장
        User dbUser = userRepository.findById(id).orElseThrow();
        objectRedisTemplate.opsForValue().set(key, dbUser);

        return dbUser;
    }

    public RedisHashUser getUser2(final Long id){
        var cachedUser = redisHashUserRepository.findById(id).orElseGet(() -> {
            User user = userRepository.findById(id).orElseThrow();
            return redisHashUserRepository.save(RedisHashUser.builder()
                    .Id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .createAt(user.getCreateAt())
                    .updatedAt(user.getUpdatedAt())
                    .build());
        });

        return cachedUser;
    }

    @Cacheable(cacheNames = CACHE1, key = "'user:' + #id")
    public User getUser3(final Long id){
        return userRepository.findById(id).orElseThrow();
    }
}
