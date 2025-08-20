package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "My_Route")
public class MyRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_route_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    // 일단 erd에 따라서 추가하기는 했으나 route_id를 통해 ai_response_id에 접근 가능 띠리서 중복 데이터 발생가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_response_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @Builder
    public MyRoute(User user, Route route, AiRouteRecommend aiRouteRecommend) {
        this.user = user;
        this.route = route;
        this.aiRouteRecommend = aiRouteRecommend;
    }
}