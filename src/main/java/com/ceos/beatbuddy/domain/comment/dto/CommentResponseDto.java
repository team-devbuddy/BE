package com.ceos.beatbuddy.domain.comment.dto;

import com.ceos.beatbuddy.domain.comment.entity.Comment;
import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        String content,
        Boolean isAnonymous,
        Long replyId,
        String memberName,
        Integer likes,
        LocalDateTime createdAt) {
    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.isAnonymous(),
                comment.getReply() != null ? comment.getReply().getId() : null,
                comment.isAnonymous() ? "익명" : comment.getMember().getRealName(),
                comment.getLikes(),
                comment.getCreatedAt()
        );
    }
}
