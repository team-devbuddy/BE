package com.ceos.beatbuddy.domain.post.dto;

import com.ceos.beatbuddy.domain.post.entity.Post;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponsePostDto {
    private Long id;
    private String title;
    private int likes;
    private int comments;
    private LocalDate createAt;
    private String nickname;

    public static ResponsePostDto of(Post post){
        ResponsePostDto.ResponsePostDtoBuilder builder = ResponsePostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .createAt(post.getCreatedAt().toLocalDate())
                .likes(post.getLikes())
                .comments(post.getComments());

        if (post.isAnonymous()) {
            builder.nickname(post.getMember().getNickname());
        }

        return builder.build();
    }
}
