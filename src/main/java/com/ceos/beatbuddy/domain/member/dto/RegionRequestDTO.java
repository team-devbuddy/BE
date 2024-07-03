package com.ceos.beatbuddy.domain.member.dto;

import com.ceos.beatbuddy.domain.member.constant.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegionRequestDTO {
    private String regions;
}
