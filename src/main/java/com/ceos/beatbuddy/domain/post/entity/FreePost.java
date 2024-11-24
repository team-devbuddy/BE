package com.ceos.beatbuddy.domain.post.entity;

import com.ceos.beatbuddy.domain.venue.entity.Venue;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

@SuperBuilder
@MappedSuperclass
public class FreePost extends Post{
    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    @JoinColumn(name = "venue_id")
    private Venue venue;
}
