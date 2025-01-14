package com.ceos.beatbuddy.domain.venue.application;

import com.ceos.beatbuddy.domain.member.dto.AdminResponseDto;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.global.CustomException;
import com.ceos.beatbuddy.global.config.jwt.TokenProvider;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshToken;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public Long createAdmin(String id) {
        if (memberRepository.existsByLoginId(id)) {
            throw new CustomException(MemberErrorCode.LOGINID_ALREADY_EXIST);
        }

        Member member = Member.builder()
                .loginId(id)
                .role("ADMIN")
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getMemberId();
    }

    @Transactional
    public ResponseEntity<AdminResponseDto> createAdminToken(Long memberId, String loginId) {

        RefreshToken byUserId = refreshTokenRepository.findByUserId(memberId);

        if (byUserId != null) {
            refreshTokenRepository.delete(byUserId);
        }

        String access = tokenProvider.createToken("access", memberId, loginId, "ADMIN", 1000 * 60 * 60 * 2L);
        String refresh = tokenProvider.createToken("refresh", memberId, loginId, "ADMIN", 1000 * 3600 * 24 * 14L);

        RefreshToken refreshToken = new RefreshToken(refresh, memberId);
        refreshTokenRepository.save(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh", refresh)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(60 * 60 * 24 * 14)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().headers(headers).body(AdminResponseDto.builder()
                .access(access)
                .build());
    }

    public Long findAdmin(String id) {
        Member member = memberRepository.findByLoginId(id).orElseThrow(
                () -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST)
        );

        return member.getMemberId();
    }
}
