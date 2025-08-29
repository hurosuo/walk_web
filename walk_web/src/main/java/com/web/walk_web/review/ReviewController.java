package com.web.walk_web.review;

import com.web.walk_web.domain.dto.ReviewDto;
import com.web.walk_web.domain.entity.User;
import com.web.walk_web.user.UserRepository;  // User 조회용 임포트 (review/ 내에서 호출 가능)
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walk/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserRepository userRepository;  // 추가: User 조회용 (review/ 내에서만 사용)

    @PostMapping
    public ResponseEntity<ReviewDto.Response> create(@RequestBody ReviewDto.CreateRequest dto, HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");  // 세션에서 Long ID 가져옴 (기존 호환)
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        User user = userRepository.findById(userId)  // ID로 User 조회 (에러 해결)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        ReviewDto.Response response = reviewService.createReview(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewDto.Response>> getReviews(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") double lat,
            @RequestParam(defaultValue = "0") double lng,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewDto.Response> reviews = reviewService.getReviews(sort, lat, lng, page, size);
        return ResponseEntity.ok(reviews);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<ReviewDto.Response> incrementLike(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");  // Long ID 가져옴
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        User user = userRepository.findById(userId)  // 조회 (에러 해결)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        // like 로직 (기존 유지, User 필요 시 사용 – 현재 미사용 but 인증용)
        ReviewDto.Response response = reviewService.incrementLikeReview(id);
        return ResponseEntity.ok(response);
    }
}