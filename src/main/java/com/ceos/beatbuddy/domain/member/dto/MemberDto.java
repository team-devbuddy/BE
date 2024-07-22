package com.ceos.beatbuddy.domain.member.dto;

import com.ceos.beatbuddy.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberDto {

    private Long memberId;
    private String role;
    private String name;
    private String nickname;
    private String loginId;

    public static MemberDto of(Member member){
        return MemberDto.builder()
                //.name(member.getName())
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }
}
