package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Point")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @Column(name = "point_x", nullable = false)
    private Double pointX;

    @Column(name = "point_y", nullable = false)
    private Double pointY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @Builder
    public Point(Double pointX, Double pointY, AiRouteRecommend aiRouteRecommend) {
        this.pointX = pointX;
        this.pointY = pointY;
        this.aiRouteRecommend = aiRouteRecommend;
    }
}