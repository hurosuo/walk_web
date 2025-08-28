package com.web.walk_web.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 모든 필드를 받는 생성자를 만듭니다.
public class UserStatsDto {
    private Long monthlyWalks;    // 이번 달 산책 횟수
    private Long totalWalks;      // 총 산책 횟수
    private Double totalDistance; // 총 거리 (km)
    private Long totalHours;      // 총 시간 (시간)
}