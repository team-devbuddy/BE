package com.ceos.beatbuddy.global.config.oauth.dto;

import com.ceos.beatbuddy.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Oauth2MemberDto {

    private Long memberId;
    private String role;
    private String name;
    private String nickname;
    private String loginId;

    public static Oauth2MemberDto of(Member member){
        return Oauth2MemberDto.builder()
                .memberId(member.getMemberId())
                .name(member.getRealName())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }
}
