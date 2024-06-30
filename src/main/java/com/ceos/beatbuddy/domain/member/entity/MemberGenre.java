package com.ceos.beatbuddy.domain.member.entity;

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
public class MemberGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberGenreId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Transient
    private Vector preferenceVector;

    @Lob
    private String preferenceVectorString;

    public void setPreferenceVector(Vector vector) {
        this.preferenceVector = vector;
        this.preferenceVectorString = vector.getElements().toString();
    }

    public Vector getPreferenceVector() {
        if (preferenceVector == null && preferenceVectorString != null) {
            List<Double> elements = List.of(preferenceVectorString.replace("[", "").replace("]", "").split(","))
                    .stream().map(String::trim).map(Double::parseDouble).collect(Collectors.toList());
            preferenceVector = new Vector(elements);
        }
        return preferenceVector;
    }
}
