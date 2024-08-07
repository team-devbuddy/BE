package com.ceos.beatbuddy.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberVectorResponseDTO {
    Long memberId;
    Long vectorId;
    String loginId;
    String nickname;
    String realName;
    String vectorString;
}
