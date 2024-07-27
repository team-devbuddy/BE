package com.ceos.beatbuddy.global.config.jwt.redis;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    void deleteByRefreshToken(String refresh);
    RefreshToken findByAccessToken(String accessToken);
}
