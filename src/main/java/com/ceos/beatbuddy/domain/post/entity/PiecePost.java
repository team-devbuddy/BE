package com.ceos.beatbuddy.domain.post.entity;


import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PiecePost extends Post{
    @ManyToOne
    @JoinColumn(name = "piece_id")
    private Piece piece;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Builder
    public PiecePost(String title, String content, Boolean anonymous,
                     List<String> imageUrls, Member member,
                     Piece piece, Venue venue) {
        super(title, content, anonymous, imageUrls, member);
        this.piece = piece;
        this.venue = venue;
    }

}
