package com.web.walk_web.domain.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TMapDto {
    private String jibunAddress;    // 지번 주소(예: 서울특별시 동대문구 ...)
    private double latitude;        // 위도
    private double longitude;       // 경도
    private boolean isDongdaemoon;  // 동대문구 여부

    public static boolean isDongdaemunByAddress(String addr) {
        if (addr == null) return false;
        // '동대문구' 문자열 포함 여부로 1차 판별
        return addr.contains("동대문구");
    }
}
