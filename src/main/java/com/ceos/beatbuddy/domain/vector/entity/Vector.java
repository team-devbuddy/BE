package com.ceos.beatbuddy.domain.vector.entity;
import com.ceos.beatbuddy.domain.vector.exception.VectorErrorCode;
import com.ceos.beatbuddy.domain.vector.exception.VectorException;
import com.ceos.beatbuddy.global.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Vector {
    private static final List<String> ALL_GENRES = Arrays.asList(
            "EDM", "HIPHOP_R&B", "HOUSE", "SOUL&FUNK", "TECHNO", "K-POP", "POP", "LATIN"
    );
    private static final List<String> ALL_MOODS = Arrays.asList(
            "HIP", "DARK", "EXCITING", "FUNKY", "EXOTIC", "TRENDY", "TROPICAL", "CHILLY"
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

    //GENRE -> VECTOR
    public static Vector fromGenres(Map<String, Double> preferenceMap) {
        List<Double> elements = ALL_GENRES.stream()
                .map(pref -> preferenceMap.getOrDefault(pref, 0.0))
                .collect(Collectors.toList());
        return new Vector(elements);
    }

    //MOOD -> VECTOR
    public static Vector fromMoods(Map<String, Double> preferenceMap) {
        List<Double> elements = ALL_MOODS.stream()
                .map(pref -> preferenceMap.getOrDefault(pref, 0.0))
                .collect(Collectors.toList());
        return new Vector(elements);
    }

    public static List<String> getTrueMoodElements(Vector vector) {
        List<String> trueMoods = new ArrayList<>();
        for (int i = 0; i < vector.elements.size(); i++) {
            if (vector.elements.get(i) == 1.0) {
                if (i < ALL_MOODS.size()) {
                    trueMoods.add(ALL_MOODS.get(i));
                } else {break;}
            }
        }
        return trueMoods;
    }

    public static List<String> getTrueGenreElements(Vector vector) {
        List<String> trueGenres = new ArrayList<>();
        for (int i = 0; i < vector.elements.size(); i++) {
            if (vector.elements.get(i) == 1.0) {
                if (i < ALL_GENRES.size()) {
                    trueGenres.add(ALL_MOODS.get(i));
                } else {break;}
            }
        }
        return trueGenres;
    }

    // 특정 장르의 인덱스 찾기
    public static int getGenreIndex(String genre) {
        int idx = ALL_GENRES.indexOf(genre);
        if(idx==-1) {
            throw new CustomException(VectorErrorCode.INDEX_NOT_EXIST);
        }
        else return idx;
    }
}

