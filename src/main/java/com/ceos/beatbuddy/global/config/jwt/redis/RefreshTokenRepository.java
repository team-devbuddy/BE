package com.ceos.beatbuddy.global.config.jwt.redis;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Boolean existsByRefreshToken(String refresh);

    void deleteByRefreshToken(String refresh);
}
