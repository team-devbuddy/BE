package com.ceos.beatbuddy.domain.member.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    TYPE1(0, "MALE"),
    TYPE2(1, "FEMALE");

    private Integer idx;
    private String text;

    Gender(Integer idx, String text) {
        this.idx = idx;
        this.text = text;
    }

    @JsonCreator
    public static Gender fromText(String text) {
        for (Gender gender : Gender.values()) {
            if (gender.getText().equals(text)) {
                return gender;
            }
        }
        return null;
    }

    public Integer getIdx() {
        return idx;
    }

    @JsonValue
    public String getText() {
        return text;
    }

    public static Gender fromIdx(Integer idx) {
        for (Gender gender : Gender.values()) {
            if (gender.getIdx().equals(idx)) {
                return gender;
            }
        }
        return null;
    }
}
