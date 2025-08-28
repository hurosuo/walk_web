package com.web.walk_web.review;

import com.web.walk_web.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserId(Long userId);  // User FK 반영

    List<Review> findByRouteId(Long routeId);

    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findAllLatest(Pageable pageable);

    @Query("SELECT r FROM Review r ORDER BY (r.likeCount - r.hateCount) DESC")
    Page<Review> findAllPopular(Pageable pageable);

    @Query("SELECT r FROM Review r JOIN MyRoute mr ON r.route.id = mr.route.id ORDER BY mr.rating DESC")  // 별점순 단순
    Page<Review> findAllByRating(Pageable pageable);

    @Query(value = "SELECT * FROM Review r " +
            "JOIN Route rt ON r.route_id = rt.route_id " +
            "JOIN Ai_route_recommend ai ON rt.ai_response_id = ai.ai_response_id " +
            "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(ai.route_start_Y)) * " +
            "cos(radians(ai.route_start_X) - radians(:lng)) + sin(radians(:lat)) * sin(radians(ai.route_start_Y)))) < 0.2",
            nativeQuery = true)
    Page<Review> findByLocation(@Param("lat") double lat, @Param("lng") double lng, Pageable pageable);
}