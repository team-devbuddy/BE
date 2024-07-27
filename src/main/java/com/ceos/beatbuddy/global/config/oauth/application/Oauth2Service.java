package com.ceos.beatbuddy.global.config.oauth.application;

import com.ceos.beatbuddy.domain.member.application.MemberService;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.global.CustomException;
import com.ceos.beatbuddy.global.config.jwt.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class Oauth2Service {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${kakao.admin}")
    private String adminKey;

    private static final String logOutUrl = "https://kapi.kakao.com/v1/user/logout";
    private static final String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

    public ResponseEntity<String> logout(Long memberId) {
        return getResponseEntity(memberId, logOutUrl);
    }

    public ResponseEntity<String> resign(Long memberId) {
        memberService.deleteMember(memberId);
        return getResponseEntity(memberId, unlinkUrl);
    }


    private ResponseEntity<String> getResponseEntity(Long memberId, String logOutUrl) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + adminKey);

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST)
        );
        String targetId = member.getLoginId().split("_")[1];
        String requestBody = "target_id_type=user_id&target_id=" + targetId;

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(logOutUrl, HttpMethod.POST, entity, String.class);

        return responseEntity;
    }
}
