package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @Column(name = "feedback_positive")
    private Boolean isPositive;

    @Column(name = "feedback_noisy")
    private Boolean isNoisy;

    @Column(name = "feedback_dirty")
    private Boolean isDirty;

    @Column(name = "feedback_dangerous")
    private Boolean isDangerous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    // 데이터 중복 문제 가능 ('ai_responsc_id')가 있는 부분
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_responsc_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @Builder
    public Feedback(Boolean isPositive, Boolean isNoisy, Boolean isDirty, Boolean isDangerous, Route route, AiRouteRecommend aiRouteRecommend) {
        this.isPositive = isPositive;
        this.isNoisy = isNoisy;
        this.isDirty = isDirty;
        this.isDangerous = isDangerous;
        this.route = route;
        this.aiRouteRecommend = aiRouteRecommend;
    }
}