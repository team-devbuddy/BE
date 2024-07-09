package com.ceos.beatbuddy.global.config.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class LoginResponseDto {
    private Long memberId;
    private String loginId;
    private String username;
}
