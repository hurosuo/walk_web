package com.web.walk_web.myroute;

import com.web.walk_web.domain.entity.MyRoute;
import org.springframework.data.domain.Sort; // Pageable 대신 Sort를 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Page 대신 List를 import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
@Repository
public interface MyRouteRepository extends JpaRepository<MyRoute, Long> {

    // 반환 타입을 Page<MyRoute>에서 List<MyRoute>로 변경
    // 파라미터를 Pageable에서 Sort로 변경
    List<MyRoute> findByUser_Id(Long userId, Sort sort);

    /**
     * 특정 사용자의 '나의 경로' 목록을 페이징 및 정렬하여 조회합니다.
     */
    Page<MyRoute> findByUser_Id(Long userId, Pageable pageable);

    // -- [ 통계 기능 쿼리 ] --

    /**
     * 특정 사용자의 이번 달 총 산책 기록 수를 계산합니다.
     */
    @Query("SELECT count(mr) FROM MyRoute mr JOIN mr.route r WHERE mr.user.id = :userId AND r.createdAt BETWEEN :startOfMonth AND :endOfMonth")
    Long countMonthlyWalks(@Param("userId") Long userId, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    /**
     * 특정 사용자의 전체 산책 기록 수를 계산합니다.
     */
    @Query("SELECT count(mr) FROM MyRoute mr WHERE mr.user.id = :userId")
    Long countTotalWalks(@Param("userId") Long userId);

    /**
     * 특정 사용자의 총 산책 거리(km) 합계를 계산합니다.
     */
    @Query("SELECT SUM(r.aiRouteRecommend.distanceInKm) FROM MyRoute mr JOIN mr.route r WHERE mr.user.id = :userId")
    Double sumTotalDistance(@Param("userId") Long userId);

    /**
     * 특정 사용자의 총 산책 시간(초 단위) 합계를 계산합니다. (Native Query로 수정)
     */
    @Query(
            value = "SELECT SUM(TIMESTAMPDIFF(SECOND, arr.route_start_time, arr.route_end_time)) " +
                    "FROM my_route mr " +
                    "JOIN route r ON mr.route_id = r.route_id " +
                    "JOIN ai_route_recommend arr ON r.ai_response_id = arr.ai_response_id " +
                    "WHERE mr.user_id = :userId",
            nativeQuery = true)
    Long sumTotalDurationInSeconds(@Param("userId") Long userId);
}