package com.ceos.beatbuddy.domain.post.entity;

import static lombok.AccessLevel.PROTECTED;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@NoArgsConstructor(access = PROTECTED)
@Entity
public class FreePost extends Post{
    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Builder
    public FreePost(String title, String content, Boolean anonymous,
                    List<String> imageUrls, Member member, Venue venue) {
        super(title, content, anonymous, imageUrls, member);
        this.venue = venue;
    }
}
