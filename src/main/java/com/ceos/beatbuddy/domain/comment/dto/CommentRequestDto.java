package com.ceos.beatbuddy.domain.comment.dto;


public record CommentRequestDto(
        String content,
        Boolean isAnonymous
) {

}
