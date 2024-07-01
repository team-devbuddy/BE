package com.ceos.beatbuddy.domain.venue.entity;

import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Venue extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String englishName;
    private String koreanName;

    @Transient
    private Vector featureVector;

    @Lob
    private String featureVectorString;

    public void setFeatureVector(Vector vector) {
        this.featureVector = vector;
        this.featureVectorString = vector.getElements().toString();
    }

    public Vector getFeatureVector() {
        if (featureVector == null && featureVectorString != null) {
            List<Double> elements = List.of(featureVectorString.replace("[", "").replace("]", "").split(","))
                    .stream().map(String::trim).map(Double::parseDouble).collect(Collectors.toList());
            featureVector = new Vector(elements);
        }
        return featureVector;
    }
}
