package com.ceos.beatbuddy.domain.member.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Region {

    TYPE1(0, "HONGDAE", "홍대"),
    TYPE2(1, "ITAEWON", "이태원"),
    TYPE3(2, "APGUJEONG", "압구정"),
    TYPE4(3, "GANGNAM/SINSA", "강남/신사"),
    TYPE5(4, "OTHERS", "기타");

    private Integer idx;
    private String text;
    private String korText;

    Region(Integer idx, String text, String korText) {
        this.idx = idx;
        this.text = text;
        this.korText = korText;
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

    public String getKorText() {
        return korText;
    }

    public String toText() {
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

    public static Region fromKorText(String korText) {
        for (Region region : Region.values()) {
            if (region.getKorText().equals(korText)) {
                return region;
            }
        }
        return null;
    }
}
