package com.web.walk_web.route;

import com.web.walk_web.domain.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    // 추가 쿼리 필요 시 (e.g. findByAiResponseId)
}