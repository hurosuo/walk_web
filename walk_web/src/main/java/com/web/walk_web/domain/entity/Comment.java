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
@Table(name = "Comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "comment_content", nullable = false, length = 2000)
    private String content;

    @Column(name = "comment_like")
    private Integer likeCount;

    @Column(name = "comment_hate")
    private Integer hateCount;

    @CreationTimestamp
    @Column(name = "comment_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // 같은 이유로 데이터 불일치 발생 가능 MyRoute 엔티티 참고
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_response_id", nullable = false)
    private AiRouteRecommend aiRouteRecommend;

    @Builder
    public Comment(String content, Integer likeCount, Integer hateCount, Review review, AiRouteRecommend aiRouteRecommend) {
        this.content = content;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.review = review;
        this.aiRouteRecommend = aiRouteRecommend;
    }
}