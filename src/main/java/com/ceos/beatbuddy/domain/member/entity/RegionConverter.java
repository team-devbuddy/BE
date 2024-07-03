package com.ceos.beatbuddy.domain.member.entity;

import com.ceos.beatbuddy.domain.member.constant.Region;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class RegionConverter implements AttributeConverter<List<Region>, String> {

    private static final String SPLIT_CHAR = ",";

    //DB에 저장될 때 사용
    @Override
    public String convertToDatabaseColumn(List<Region> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(Region::getText)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    //DB의 데이터를 Object로 매핑할 때 사용
    @Override
    public List<Region> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .map(Region::fromText)
                .collect(Collectors.toList());
    }
}

