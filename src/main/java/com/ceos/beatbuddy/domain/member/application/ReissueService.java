package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.global.config.jwt.redis.RefreshToken;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReissueService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(Long userId, String newRefresh) {

        RefreshToken refreshToken = new RefreshToken(newRefresh, userId);

        refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(String refresh) {
        refreshTokenRepository.deleteByRefreshToken(refresh);
    }
}
