package com.ceos.beatbuddy.domain.post.dto;

import com.ceos.beatbuddy.global.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.List;
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
public class PiecePostRequestDto extends BaseTimeEntity {
    private String title;
    private String content;
    private List<MultipartFile> images;
    private Long venueId;
    private int totalPrice;
    private int totalMembers;
    private LocalDateTime eventDate;
}
