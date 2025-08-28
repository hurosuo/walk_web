package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Ai_route_recommend")
public class AiRouteRecommend {

    public enum RouteDuration {
        MIN_15, // 15분
        MIN_30, // 30분
        MIN_45, // 45분
        MIN_60  // 60분
    }
    public enum RoutePurpose {
        CITY,       // 도심
        NIGHT_VIEW, // 야경
        QUIET,      // 조용
        SCENERY     // 경치
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_response_id")
    private Long id;

    @Column(name = "route_start_X", nullable = false)
    private Double routeStartX;

    @Column(name = "route_start_Y", nullable = false)
    private Double routeStartY;

    @Column(name = "route_start_time", nullable = false)
    private LocalDateTime routeStartTime;

    @Column(name = "route_end_time", nullable = false)
    private LocalDateTime routeEndTime;

    @Column(name = "route_title", nullable = false)
    private String title; // 산책 제목

    @Column(name = "route_content", nullable = false, length = 2000) // 내용이 길 수 있으므로 length 지정
    private String content; // 산책 내용

    @Column(name = "ai_summary", nullable = false, length = 1000)
    private String summary; // AI 요약

    @Enumerated(EnumType.STRING) // Enum의 이름을 DB에 문자열로 저장
    @Column(name = "route_duration", nullable = false)
    private RouteDuration duration; // 산책 시간

    @Enumerated(EnumType.STRING)
    @Column(name = "route_purpose", nullable = false)
    private RoutePurpose purpose; // 산책 목적

    @Builder
    public AiRouteRecommend(String title, String content, String summary, RouteDuration duration, RoutePurpose purpose, Double routeStartX, Double routeStartY, LocalDateTime routeStartTime, LocalDateTime routeEndTime) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.duration = duration;
        this.purpose = purpose;
        this.routeStartX = routeStartX;
        this.routeStartY = routeStartY;
        this.routeStartTime = routeStartTime;
        this.routeEndTime = routeEndTime;
    }
}