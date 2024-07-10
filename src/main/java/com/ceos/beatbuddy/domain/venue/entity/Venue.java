package com.ceos.beatbuddy.domain.venue.entity;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

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

    private Region region;
    private String description;
    private String address;

    @Builder.Default
    private Long heartbeatNum = 0L;

    public void addHeartbeatNum() {
        this.heartbeatNum += 1;
    }

    public void deleteHeartbeatNum() {
        if(this.heartbeatNum > 0) {
            this.heartbeatNum -= 1;
        }
    }
}
