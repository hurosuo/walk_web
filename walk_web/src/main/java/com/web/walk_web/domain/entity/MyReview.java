package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "My_Review")
public class MyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // 마찬가지의 이유로 ai_response_id 중복 문제 일어날 가능성 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_response_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @Builder
    public MyReview(User user, Review review, AiRouteRecommend aiRouteRecommend) {
        this.user = user;
        this.review = review;
        this.aiRouteRecommend = aiRouteRecommend;
    }
}