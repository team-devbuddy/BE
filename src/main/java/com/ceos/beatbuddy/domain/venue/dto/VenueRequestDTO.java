package com.ceos.beatbuddy.domain.venue.dto;

import com.ceos.beatbuddy.domain.member.constant.Region;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class VenueRequestDTO {
    private String englishName;
    private String koreanName;

    private Region region;
    private String operationHours;
    private String description;
    private String address;
    private String insta;
    private String phoneNum;
}
