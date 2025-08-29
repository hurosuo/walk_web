package com.web.walk_web.domain.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {

    private String title;            // 코스 제목
    private String content;          // 코스 설명
    private String summary;          // 요약
    private double distanceInKm;     // 총 거리 (KM 단위)
    private String duration;         // MIN_15, MIN_30, MIN_45, MIN_60
    private String purpose;          // CITY, NIGHT_VIEW, QUIET, SCENERY
    private String addressJibun;     // 지번 주소
    private boolean withPet;         // 반려동물 동반 여부
    private Integer cross;           // 횡단보도 개수

    private double routeStartX;      // 시작 경도
    private double routeStartY;      // 시작 위도

    private List<PointDto> points;   // 경로 포인트 리스트

    // 내부 Point DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PointDto {
        private double pointX; // 경도
        private double pointY; // 위도
    }
}
