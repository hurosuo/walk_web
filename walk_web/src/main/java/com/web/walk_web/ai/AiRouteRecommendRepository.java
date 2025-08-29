package com.web.walk_web.ai;

import com.web.walk_web.domain.entity.AiRouteRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRouteRecommendRepository extends JpaRepository<AiRouteRecommend, Long> {
}