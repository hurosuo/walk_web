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
@Table(name = "Review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "review_title", nullable = false, length = 50)
    private String title;

    @Column(name = "review_content", nullable = false, length = 2000)
    private String content;

    @Column(name = "review_like")
    private Integer likeCount;

    @Column(name = "rcview_hate")
    private Integer hateCount;

    @CreationTimestamp
    @Column(name = "review_create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    // MyRoute와 마찬가지로 route_id를 통해 ai_response_id 접근할 수 있어 중복 데이터 발생할 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_response_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @Builder
    public Review(String title, String content, Integer likeCount, Integer hateCount, Route route, AiRouteRecommend aiRouteRecommend) {
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.route = route;
        this.aiRouteRecommend = aiRouteRecommend;
    }
}