package com.ceos.beatbuddy.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,  // URL의 type을 사용할 것이므로 JSON 내부에는 type 정보 불필요
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PostRequestDto.FreePostRequestDto.class, name = "free"),
        @JsonSubTypes.Type(value = PostRequestDto.PiecePostRequestDto.class, name = "piece")
})
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
    ) implements PostRequestDto {
    }

    @Builder
    record FreePostRequestDto(
            String title,
            String content,
            List<MultipartFile> images,
            Long venueId
    ) implements PostRequestDto {
    }
}