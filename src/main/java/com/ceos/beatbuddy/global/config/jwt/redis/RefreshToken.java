package com.ceos.beatbuddy.global.config.jwt.redis;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 14)
public class RefreshToken {
    @Id
    private String refreshToken;
    private String username;

    public RefreshToken(String refreshToken, String username) {
        this.refreshToken = refreshToken;
        this.username = username;
    }
}
