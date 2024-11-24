package com.ceos.beatbuddy.domain.post.entity;


import com.ceos.beatbuddy.domain.venue.entity.Venue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@MappedSuperclass
public class PiecePost extends Post{
    @ManyToOne
    @JoinColumn(name = "piece_id")
    private Piece piece;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

}
