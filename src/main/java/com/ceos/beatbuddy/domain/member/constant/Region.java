package com.ceos.beatbuddy.domain.member.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Region {

    TYPE1(0, "HONGDAE"),
    TYPE2(1, "ITAEWON"),
    TYPE3(2, "SINSA"),
    TYPE4(3, "APGUJEONG");

    private Integer idx;
    private String text;

    Region(Integer idx, String text) {
        this.idx = idx;
        this.text = text;
    }

    @JsonCreator
    public static Region fromText(String text) {
        for (Region region : Region.values()) {
            if (region.getText().equals(text)) {
                return region;
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

    public static Region fromIdx(Integer idx) {
        for (Region region : Region.values()) {
            if (region.getIdx().equals(idx)) {
                return region;
            }
        }
        return null;
    }
}
