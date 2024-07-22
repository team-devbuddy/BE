package com.ceos.beatbuddy.domain.vector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Map;

@Getter
public class MoodRequestDTO {
    @NotNull
    @Schema(description = "\"HIP\", \"DARK\", \"EXCITING\", \"FUNKY\", \"EXOTIC\", \"TRENDY\", \"TROPICAL\", \"CHILLY\" 의 값들을 각각 기입해주세요." +
            "이들의 기입 순서는 상관없습니다. 안에서 알아서 매칭해줍니다.",
            example = "{\"HIP\": 1.0, \"EXCITING\": 0.0, \"FUNKY\":0.0, \"EXOTIC\":1.0, \"TRENDY\":1.0, \"TROPICAL\":1.0, \"CHILLY\":0.0}")
    private Map<String, Double> moodPreferences;
}
