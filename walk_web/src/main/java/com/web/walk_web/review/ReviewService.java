package com.web.walk_web.review;

import com.web.walk_web.domain.dto.ReviewDto;
import com.web.walk_web.domain.entity.Review;
import com.web.walk_web.domain.entity.Route;
import com.web.walk_web.domain.entity.User;
import com.web.walk_web.route.RouteRepository;  // RouteRepository 임포트
import com.web.walk_web.user.UserRepository;  // 변화: UserRepository 임포트 추가 (user/ 패키지)
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
    private final UserRepository userRepository;  // 변화: UserRepository 주입 추가 (final로 자동 @Autowired)

    @Transactional
    public ReviewDto.Response createReview(ReviewDto.CreateRequest dto, Long userId) {  // 변화: User → Long userId 파라미터
        User user = userRepository.findById(userId)  // userId로 User 조회
                .orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID"));
        Review review = dto.toEntity(route, route.getAiRouteRecommend(), user);
        Review saved = reviewRepository.save(review);
        return new ReviewDto.Response(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto.Response> getReviews(String sort, double lat, double lng, int page, int size) {
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
}