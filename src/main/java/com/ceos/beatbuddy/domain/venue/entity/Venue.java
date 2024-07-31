package com.ceos.beatbuddy.domain.venue.entity;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.venue.dto.VenueRequestDTO;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;
import java.util.Map;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    private String englishName;
    private String koreanName;

    @Enumerated
    private Region region;
    private boolean isSmokingAllowed;
    private String description;
    private String address;
    private String instaId;
    private String instaUrl;
    private String phoneNum;

    @ElementCollection
    private Map<String,String> operationHours;

    private String logoUrl;
    @ElementCollection
    private List<String> backgroundUrl;

    @Builder.Default
    private Long heartbeatNum = 0L;

    public void addHeartbeatNum() {
        if(this.heartbeatNum==null) {
            this.heartbeatNum = 1L;
        }
        this.heartbeatNum += 1;
    }

    public void deleteHeartbeatNum() {
        if(this.heartbeatNum > 0) {
            this.heartbeatNum -= 1;
        }
    }

    public static Venue of(VenueRequestDTO request, String  logoUrl, List<String> backgroundUrl){
        return Venue.builder()
                .isSmokingAllowed(request.isSmokingAllowed())
                .englishName(request.getEnglishName())
                .koreanName(request.getKoreanName())
                .region(request.getRegion())
                .address(request.getAddress())
                .description(request.getDescription())
                .phoneNum(request.getPhoneNum())
                .instaId(request.getInstaId())
                .instaUrl(request.getInstaUrl())
                .operationHours(request.getWeeklyOperationHours())
                .logoUrl(logoUrl)
                .backgroundUrl(backgroundUrl)
                .build();
    }
}
