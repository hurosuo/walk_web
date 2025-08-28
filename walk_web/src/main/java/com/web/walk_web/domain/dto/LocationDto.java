package com.web.walk_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private String jibunAddress; // 지번 주소
    private double latitude;     // 위도
    private double longitude;    // 경도
    private boolean isDongdaemun; // 동대문구 여부
}
