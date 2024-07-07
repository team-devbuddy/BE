package com.ceos.beatbuddy.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class SearchDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDTO {
        private List<String> keyword;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDTO {
        private Long id;
        private String nickname;
        private String title;
        private String content;
        private String address;
        private String myItem;
        private String exchangeItem;
        private String profileImg;
        private List<String> images;
        private List<BookmarkResponseDto> bookmarks;
        private String currentState;
        private String createAt;
        private int bookmarkCount;
        private int commentCount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalResponseDTO {
        private Long postCnt;
        private List<ResponseDTO> posts;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkResponseDto {
        private Long id;
        private String name;
        // 다른 필요한 필드들
    }
}
