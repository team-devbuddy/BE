package com.ceos.beatbuddy.domain.heartbeat.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HeartbeatResponseDTO {

    private Long heartId;
    private Long memberId;
    private Long venueId;

}
