package com.example.cache.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@RedisHash(value = "redishash-user", timeToLive = 30L)
public class RedisHashUser {

    @Id
    private Long Id;

    private String name;

    @Indexed
    private String email;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
}
