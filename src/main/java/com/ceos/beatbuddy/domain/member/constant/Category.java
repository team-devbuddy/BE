package com.ceos.beatbuddy.domain.member.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    TYPE1(0, "USER"),
    TYPE2(1, "MD"),
    TYPE3(2, "OWNER");

    private Integer idx;
    private String text;

    Category(Integer idx, String text) {
        this.idx = idx;
        this.text = text;
    }

    @JsonCreator
    public static Category fromText(String text) {
        for (Category category : Category.values()) {
            if (category.getText().equals(text)) {
                return category;
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

    public static Category fromIdx(Integer idx) {
        for (Category category : Category.values()) {
            if (category.getIdx().equals(idx)) {
                return category;
            }
        }
        return null;
    }
}
