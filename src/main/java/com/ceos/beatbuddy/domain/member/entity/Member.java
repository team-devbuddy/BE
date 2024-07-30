package com.ceos.beatbuddy.domain.member.entity;

import com.ceos.beatbuddy.domain.member.constant.Gender;
import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String loginId;


    private String nickname;
    private String realName;

    private Gender gender;

    @Convert(converter = RegionConverter.class)
    private List<Region> regions;

    private LocalDate dateOfBirth;

    private String role;

    @Builder.Default
    private Boolean setNewNickname = false;
    @Builder.Default
    private Boolean isAdult= false;
    @Builder.Default
    private Boolean isLocationConsent = false;
    @Builder.Default
    private Boolean isMarketingConsent = false;

    private Long latestArchiveId;

    public void saveConsents(Boolean isLocationConsent, Boolean isMarketingConsent) {
        this.isLocationConsent = isLocationConsent;
        this.isMarketingConsent = isMarketingConsent;
    }

    public void saveNickname(String nickname) {
        this.nickname = nickname;
        this.setNewNickname = true;
    }

    public void saveRegions(List<Region> regions) {
        this.regions = regions;
    }

    public void setAdultUser(){
        this.isAdult = true;
    }

    public void saveLatestArchiveId(Long latestArchiveId) {this.latestArchiveId = latestArchiveId;}

}
