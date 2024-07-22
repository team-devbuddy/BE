package com.ceos.beatbuddy.domain.venue.entity;


import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VenueMood extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueMoodId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "venueId")
    private Venue venue;

    @Transient
    private Vector moodVector;

    @Lob
    private String moodVectorString;

    public void updateMoodVector(Vector vector) {
        this.moodVector = vector;
        this.moodVectorString = vector.getElements().toString();
    }

    public Vector getMoodVector() {
        if (moodVector == null && moodVectorString != null) {
            List<Double> elements = List.of(moodVectorString.replace("[", "").replace("]", "").split(","))
                    .stream().map(String::trim).map(Double::parseDouble).collect(Collectors.toList());
            moodVector = new Vector(elements);
        }
        return moodVector;
    }

}
