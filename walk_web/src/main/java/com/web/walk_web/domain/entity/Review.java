package com.web.walk_web.domain.entity;  // 엔티티는 domain.entity 유지 (구조 이미지)

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
    private Integer likeCount = 0;

    @Column(name = "review_hate")
    private Integer hateCount = 0;

    @CreationTimestamp
    @Column(name = "review_create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_response_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // 변화: User FK 추가
    private User user;

    @Builder
    public Review(String title, String content, Route route, AiRouteRecommend aiRouteRecommend, User user) {
        this.title = title;
        this.content = content;
        this.route = route;
        this.aiRouteRecommend = aiRouteRecommend;
        this.user = user;
    }
}