package com.ceos.beatbuddy.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

public sealed interface PostRequestDto {
    String title();
    String content();
    List<MultipartFile> images();
    Long venueId();
    @Builder
    record PiecePostRequestDto(
            String title,
            String content,
            List<MultipartFile> images,
            Long venueId,
            int totalPrice,
            int totalMembers,
            LocalDateTime eventDate
    ) implements PostRequestDto {}

    @Builder
    record FreePostRequestDto(
            String title,
            String content,
            List<MultipartFile> images,
            Long venueId
    ) implements PostRequestDto {}
}