package com.ceos.beatbuddy.global.config.oauth;

import com.ceos.beatbuddy.domain.user.application.MemberService;
import com.ceos.beatbuddy.domain.user.dto.MemberDto;
import com.ceos.beatbuddy.global.config.oauth.dto.KakaoResponse;
import com.ceos.beatbuddy.global.config.oauth.dto.OAuth2Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        OAuth2Response oAuth2Response = null;
        String userId = null;

        try {
            System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (oAuth2Response.getProvider()) {
            case "KAKAO":
                oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 공급자입니다.");
        }

        userId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        MemberDto memberDto = memberService.findOrCreateUser(userId, oAuth2Response.getName());


        return new CustomOAuth2User(memberDto);
    }
}
