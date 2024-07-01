package com.ceos.beatbuddy.domain.vector.entity;
import com.ceos.beatbuddy.domain.vector.exception.VectorErrorCode;
import com.ceos.beatbuddy.domain.vector.exception.VectorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Vector {
    private static final List<String> ALL_GENRES = Arrays.asList(
            "EDM", "HIPHOP_RANDB", "HOUSE", "SOUL_FUNK", "TECHNO", "K_POP"
    );
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

    //PREFERENCE -> VECTOR
    public static Vector fromPreferences(Map<String, Double> preferenceMap) {
        List<Double> elements = ALL_GENRES.stream()
                .map(pref -> preferenceMap.getOrDefault(pref, 0.0))
                .collect(Collectors.toList());
        return new Vector(elements);
    }

    //JSON -> PREFERENCE
    public static Vector fromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> preferenceMap = mapper.readValue(json, Map.class);
        return fromPreferences(preferenceMap);
    }
}

