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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_response_id")
    private Long id;

    @Column(name = "route_start_X", nullable = false)
    private Double routeStartX;

    @Column(name = "route_start_Y", nullable = false)
    private Double routeStartY;

    @Column(name = "route_end_X", nullable = false)
    private Double routeEndX;

    @Column(name = "route_end_Y", nullable = false)
    private Double routeEndY;

    @Column(name = "route_start_time", nullable = false)
    private LocalDateTime routeStartTime;

    @Column(name = "route_end_time", nullable = false)
    private LocalDateTime routeEndTime;

    @Builder
    public AiRouteRecommend(Double routeStartX, Double routeStartY, Double routeEndX, Double routeEndY, LocalDateTime routeStartTime, LocalDateTime routeEndTime) {
        this.routeStartX = routeStartX;
        this.routeStartY = routeStartY;
        this.routeEndX = routeEndX;
        this.routeEndY = routeEndY;
        this.routeStartTime = routeStartTime;
        this.routeEndTime = routeEndTime;
    }
}