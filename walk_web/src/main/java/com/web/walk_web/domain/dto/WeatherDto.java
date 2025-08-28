package com.web.walk_web.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WeatherDto { // 기상청 api DTO
    private String baseDateTime;       // API 기준/발표 시각 (yyyyMMdd HHmm)
    private String fcstDateTime;       // 예보 시각           (yyyyMMdd HHmm)

    private Double temperature;        // T1H (℃)
    private Integer humidity;          // REH (%)
    private Double precipitationMm;    // RN1 (mm)
    private Integer precipitationTypeCode; // PTY (0,1,2,3,5,6,7)
    private String  precipitationType; // PTY 라벨
    private Double windSpeed;          // WSD (m/s)
}
