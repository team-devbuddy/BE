package com.ceos.beatbuddy.domain.member.entity;

import com.ceos.beatbuddy.domain.member.constant.Gender;
import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Builder.Default
    private boolean isLocationConsent = false;
    @Builder.Default
    private boolean isMarketingConsent = false;

    public void saveConsents(Boolean isLocationConsent, Boolean isMarketingConsent) {
        this.isLocationConsent = isLocationConsent;
        this.isMarketingConsent = isMarketingConsent;
    }

    public void saveNickname(String nickname) {
        this.nickname = nickname;
    }

    public void saveRegions(List<Region> regions) {
        this.regions = regions;
    }
}
