package com.web.walk_web.ai;

import com.web.walk_web.domain.entity.AiRouteRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRepository extends JpaRepository<AiRouteRecommend, Long> {
}