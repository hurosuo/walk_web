package com.web.walk_web.review;

import com.web.walk_web.domain.dto.ReviewDto;
import com.web.walk_web.domain.entity.Review;
import com.web.walk_web.domain.entity.Route;
import com.web.walk_web.domain.entity.User;
import com.web.walk_web.route.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RouteRepository routeRepository;

    @Transactional
    public ReviewDto.Response createReview(ReviewDto.CreateRequest dto, User user) {
        // 기존 로직 유지
        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID"));
        Review review = dto.toEntity(route, route.getAiRouteRecommend(), user);
        Review saved = reviewRepository.save(review);
        return new ReviewDto.Response(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto.Response> getReviews(String sort, double lat, double lng, int page, int size) {
        // 기존 로직 유지
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews;
        if (lat != 0 && lng != 0) {
            reviews = reviewRepository.findByLocation(lat, lng, pageable);
        } else {
            reviews = switch (sort != null ? sort.toLowerCase() : "latest") {
                case "popular" -> reviewRepository.findAllPopular(pageable);
                case "rating" -> reviewRepository.findAllByRating(pageable);
                default -> reviewRepository.findAllLatest(pageable);
            };
        }
        return reviews.map(ReviewDto.Response::new);
    }

    // 추가: 좋아요 증가 로직 (프론트 버튼 클릭 시 호출)
    @Transactional
    public ReviewDto.Response incrementLikeReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review ID"));  // 리뷰 조회
        review.incrementLike();  // likeCount +1 (엔티티 메서드 호출)
        Review updated = reviewRepository.save(review);  // DB 업데이트
        return new ReviewDto.Response(updated);  // 업데이트된 DTO 반환 (likeCount 증가 확인)
    }
}