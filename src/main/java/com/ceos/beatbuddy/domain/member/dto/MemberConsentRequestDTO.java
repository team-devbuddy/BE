package com.ceos.beatbuddy.domain.member.dto;


import lombok.Builder;
import lombok.Getter;
import org.antlr.v4.runtime.misc.NotNull;

@Builder
@Getter
public class MemberConsentRequestDTO {
    @NotNull
    private Boolean isLocationConsent;
    @NotNull
    private Boolean isMarketingConsent;
}
