package com.ceos.beatbuddy.domain.venue.dto;

import com.ceos.beatbuddy.domain.member.constant.Region;
import java.util.Map;
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
    private boolean smokingAllowed;
    private String englishName;
    private String koreanName;

    private Region region;
    private Map<String,String> weeklyOperationHours;
    private String description;
    private String address;
    private String instaId;
    private String instaUrl;
    private String phoneNum;
}
