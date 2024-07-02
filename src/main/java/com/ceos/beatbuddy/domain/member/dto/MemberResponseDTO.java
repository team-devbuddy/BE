package com.ceos.beatbuddy.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDTO {

    private Long memberId;
    private String loginId;
    private String nickname;
}
