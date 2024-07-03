package com.ceos.beatbuddy.domain.member.entity;

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
public class MemberGenre extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberGenreId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Transient
    private Vector genreVector;

    @Lob
    private String genreVectorString;

    public void setGenreVector(Vector vector) {
        this.genreVector = vector;
        this.genreVectorString = vector.getElements().toString();
    }

    public Vector getGenreVector() {
        if (genreVector == null && genreVectorString != null) {
            List<Double> elements = List.of(genreVectorString.replace("[", "").replace("]", "").split(","))
                    .stream().map(String::trim).map(Double::parseDouble).collect(Collectors.toList());
            genreVector = new Vector(elements);
        }
        return genreVector;
    }
}
