package com.web.walk_web.review;

import com.web.walk_web.domain.dto.ReviewDto;
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

    @PostMapping
    public ResponseEntity<ReviewDto.Response> create(@RequestBody ReviewDto.CreateRequest dto, HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");  // 세션에서 userId (Long) 가져옴
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        ReviewDto.Response response = reviewService.createReview(dto, userId);  // 변화: User 대신 userId 전달 (Service에서 조회)
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
}