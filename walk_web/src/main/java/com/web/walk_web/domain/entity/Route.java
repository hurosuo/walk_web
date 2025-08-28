package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_response_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @CreationTimestamp
    @Column(name = "route_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Route(AiRouteRecommend aiRouteRecommend) {
        this.aiRouteRecommend = aiRouteRecommend;
    }
}