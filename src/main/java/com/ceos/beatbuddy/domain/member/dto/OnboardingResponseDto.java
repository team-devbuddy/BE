package com.ceos.beatbuddy.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class OnboardingResponseDto {
    @Schema(description = "장르 선택 여부")
    private boolean Genre;

    @Schema(description = "분위기 선택 여부")
    private boolean Mood;

    @Schema(description = "지역 선택 여부")
    private boolean Region;

    @Schema(description = "성인 인증 여부")
    private boolean AdultCert;


    public OnboardingResponseDto() {
        this.Genre = false;
        this.Mood = false;
        this.Region = false;
        this.AdultCert = false;
    }

    public void setGenre() {
        this.Genre = true;
    }

    public void setMood() {
        this.Mood = true;
    }

    public void setRegion() {
        this.Region = true;
    }

    public void setAdultCert() {
        this.AdultCert = true;
    }
}
