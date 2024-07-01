package com.ceos.beatbuddy.domain.venue.entity;

import com.ceos.beatbuddy.domain.vector.entity.Vector;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VenueGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueGenreId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "venueId")
    private Venue venue;

    @Transient
    private Vector genreVector;

    @Lob
    private String genreVectorString;

    public void setPreferenceVector(Vector vector) {
        this.genreVector = vector;
        this.genreVectorString = vector.getElements().toString();
    }

    public Vector getPreferenceVector() {
        if (genreVector == null && genreVectorString != null) {
            List<Double> elements = List.of(genreVectorString.replace("[", "").replace("]", "").split(","))
                    .stream().map(String::trim).map(Double::parseDouble).collect(Collectors.toList());
            genreVector = new Vector(elements);
        }
        return genreVector;
    }

}
