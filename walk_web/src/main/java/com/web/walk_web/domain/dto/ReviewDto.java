package com.web.walk_web.domain.dto;  // dto/ 폴더 (구조 이미지)

import com.web.walk_web.domain.entity.Route;
import com.web.walk_web.domain.entity.AiRouteRecommend;
import com.web.walk_web.domain.entity.User;
import com.web.walk_web.domain.entity.Review;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewDto {

    @Getter @Setter @NoArgsConstructor
    public static class CreateRequest {
        private String title;
        private String content;
        private Long routeId;  // route_id 입력 (ERD)

        public Review toEntity(Route route, AiRouteRecommend aiRouteRecommend, User user) {
            return Review.builder()
                    .title(title)
                    .content(content)
                    .route(route)
                    .aiRouteRecommend(aiRouteRecommend)
                    .user(user)  // 변화: User 세션에서
                    .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String title;
        private final String content;
        private final Integer likeCount;
        private final Integer hateCount;
        private final LocalDateTime createdAt;
        private final String userNickname;  // User FK 반영
        private final String aiSummary;  // Ai summary 포함 (동의)
        private final String aiTitle;    // Ai title 포함

        public Response(Review review) {
            this.id = review.getId();
            this.title = review.getTitle();
            this.content = review.getContent();
            this.likeCount = review.getLikeCount();
            this.hateCount = review.getHateCount();
            this.createdAt = review.getCreatedAt();
            this.userNickname = review.getUser().getNickname();
            this.aiSummary = review.getAiRouteRecommend().getSummary();
            this.aiTitle = review.getAiRouteRecommend().getTitle();
        }
    }
}