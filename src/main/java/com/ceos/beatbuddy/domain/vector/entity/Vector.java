package com.ceos.beatbuddy.domain.vector.entity;

import com.ceos.beatbuddy.domain.vector.exception.VectorErrorCode;
import com.ceos.beatbuddy.domain.vector.exception.VectorException;
import com.ceos.beatbuddy.global.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class Vector {
    private static final List<String> ALL_GENRES = Arrays.asList(
            "EDM", "HIPHOP", "HOUSE", "SOUL&FUNK", "TECHNO", "K-POP", "POP", "LATIN", "R&B", "ROCK"
    );
    private static final List<String> ALL_MOODS = Arrays.asList(
            "PUB", "CLUB", "DEEP", "CHILL", "LIGHT", "HUNTING", "EXOTIC", "ROOFTOP"
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
                } else {
                    break;
                }
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
                } else {
                    break;
                }
            }
        }
        return trueGenres;
    }

    public static String inputGenreVector(List<String> inputGenre) {
        // 벡터의 초기값을 0.0으로 설정
        Double[] genreVector = new Double[ALL_GENRES.size()];
        Arrays.fill(genreVector, 0.0);

        // 입력된 장르에 대해 해당 인덱스의 값을 1.0으로 설정
        for (String genre : inputGenre) {
            int index = getGenreIndex(genre);
            if (index == -1) {
                throw new CustomException(VectorErrorCode.UNAVAILABLE_INPUT);
            }
            genreVector[index] = 1.0;
        }

        // 벡터를 문자열 형식으로 변환
        String result = Arrays.stream(genreVector)
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));

        return result;
    }

    public static String inputMoodVector(List<String> inputMood) {
        // 벡터의 초기값을 0.0으로 설정
        Double[] moodVector = new Double[ALL_MOODS.size()];
        Arrays.fill(moodVector, 0.0);

        // 입력된 장르에 대해 해당 인덱스의 값을 1.0으로 설정
        for (String mood : inputMood) {
            int index = getMoodIndex(mood);
            if (index == -1) {
                throw new CustomException(VectorErrorCode.UNAVAILABLE_INPUT);
            }
            moodVector[index] = 1.0;
        }

        // 벡터를 문자열 형식으로 변환
        String result = Arrays.stream(moodVector)
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));

        return result;
    }

    public static int getGenreIndex(String genre) {
        int idx = ALL_GENRES.indexOf(genre);
        if (idx == -1) {
            throw new CustomException(VectorErrorCode.GENRE_INDEX_NOT_EXIST);
        } else {
            return idx;
        }
    }

    public static int getMoodIndex (String mood) {
        int idx = ALL_MOODS.indexOf(mood);
        if (idx == -1) {
            throw new CustomException(VectorErrorCode.MOOD_INDEX_NOT_EXIST);
        } else {
            return idx;
        }
    }


}

