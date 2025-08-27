package com.web.walk_web.weather;

public enum PrecipType {
    NONE(0, "없음"),
    RAIN(1, "비"),
    SLEET(2, "비/눈(진눈개비)"),
    SNOW(3, "눈"),
    DRIZZLE(5, "빗방울"),
    DRIZZLE_SNOW_BLOWING(6, "빗방울/눈날림"),
    SNOW_BLOWING(7, "눈날림"),
    UNKNOWN(-1, "알수없음");

    private final int code;
    private final String label;

    PrecipType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public String label() { return label; }

    public static PrecipType from(Integer code) {
        if (code == null) return UNKNOWN;
        for (PrecipType p : values()) {
            if (p.code == code) return p;
        }
        return UNKNOWN;
    }
}
