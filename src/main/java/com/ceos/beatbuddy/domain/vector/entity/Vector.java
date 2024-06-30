package com.ceos.beatbuddy.domain.vector.entity;
import com.ceos.beatbuddy.domain.vector.exception.VectorErrorCode;
import com.ceos.beatbuddy.domain.vector.exception.VectorException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Vector {
    private final List<Double> elements;

    public double cosineSimilarity(Vector other) {
        if (elements.size() != other.elements.size()) {
            throw new VectorException(VectorErrorCode.NOT_SAME_LENGTH);
        }

        Double dotProduct = 0.0;
        Double normA = 0.0;
        Double normB = 0.0;

        for (int i = 0; i < elements.size(); i++) {
            dotProduct += elements.get(i) * other.elements.get(i);
            normA += Math.pow(elements.get(i), 2);
            normB += Math.pow(other.elements.get(i), 2);
        }

        if (normA == 0 || normB == 0) {
            throw new VectorException(VectorErrorCode.VECTOR_ZERO_NORM);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    public static Vector fromString(String vectorString) {
        List<Double> elements = List.of(vectorString.replace("[", "").replace("]", "").split(","))
                .stream().map(String::trim).map(Double::parseDouble).collect(Collectors.toList());
        return new Vector(elements);
    }
}

