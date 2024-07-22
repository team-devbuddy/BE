package com.ceos.beatbuddy.domain.vector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Map;

@Getter
public class GenreRequestDTO {
    @NotNull
    @Schema(description = "\"EDM\", \"HIPHOP_R&B\", \"HOUSE\", \"SOUL&FUNK\", \"TECHNO\", \"K-POP\", \"POP\", \"LATIN\" 의 값들을 각각 기입해주세요." +
            "이들의 기입 순서는 상관없습니다. 안에서 알아서 매칭해줍니다.",
            example = "{\"EDM\": 0.0, \"HIPHOP_R&B\": 1.0, \"HOUSE\":0.0, \"SOUL&FUNK\":1.0, \"TECHNO\":0.0, \"K-POP\":1.0, \"POP\":0.0, \"LATIN\":1.0}")
    private Map<String, Double> genrePreferences;
}
