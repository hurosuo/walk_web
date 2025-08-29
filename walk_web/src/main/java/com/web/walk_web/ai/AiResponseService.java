package com.web.walk_web.ai;


import com.web.walk_web.domain.dto.ResponseDto;
import com.web.walk_web.domain.entity.AiRouteRecommend;
import com.web.walk_web.domain.entity.Point;
import com.web.walk_web.ai.AiRouteRecommendRepository;
import com.web.walk_web.ai.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiResponseService {

    private final AiRouteRecommendRepository aiRouteRecommendRepository;
    private final PointRepository pointRepository;

    @Transactional // 여러 DB 작업을 하나의 단위로 묶습니다. 하나라도 실패하면 모두 롤백됩니다.
    public void saveAiRoute(ResponseDto dto) {

        // 1. DTO에 없는 routeStartTime, routeEndTime 계산
        LocalDateTime startTime = LocalDateTime.now();
        // duration (예: "MIN_15")에서 숫자만 추출하여 분(minute)으로 변환
        int minutesToAdd = Integer.parseInt(dto.getDuration().replace("MIN_", ""));
        LocalDateTime endTime = startTime.plusMinutes(minutesToAdd);

        // 2. DTO -> AiRouteRecommend 엔티티로 변환
        AiRouteRecommend routeToSave = AiRouteRecommend.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .summary(dto.getSummary())
                .distanceInKm(dto.getDistanceInKm())
                // String "MIN_15"를 Enum 타입 AiRouteRecommend.RouteDuration.MIN_15로 변환
                .duration(AiRouteRecommend.RouteDuration.valueOf(dto.getDuration()))
                .purpose(AiRouteRecommend.RoutePurpose.valueOf(dto.getPurpose()))
                .routeStartX(dto.getRouteStartX())
                .routeStartY(dto.getRouteStartY())
                .addressJibun(dto.getAddressJibun())
                .withPet(dto.isWithPet())
                .routeStartTime(startTime) // 계산된 시작 시간 저장
                .routeEndTime(endTime)     // 계산된 종료 시간 저장
                .build();

        // 3. AiRouteRecommend를 먼저 저장 (⭐ Point가 참조할 ID가 생성됩니다)
        AiRouteRecommend savedRoute = aiRouteRecommendRepository.save(routeToSave);

        // 4. DTO의 PointDto 리스트 -> Point 엔티티 리스트로 변환
        List<Point> pointsToSave = new ArrayList<>();
        for (ResponseDto.PointDto pointDto : dto.getPoints()) {
            Point point = Point.builder()
                    .pointX(pointDto.getPointX())
                    .pointY(pointDto.getPointY())
                    .aiRouteRecommend(savedRoute) // ⭐ 방금 저장된 AiRouteRecommend 객체로 관계 설정 (FK)
                    .build();
            pointsToSave.add(point);
        }

        // 5. 변환된 Point 엔티티 리스트를 DB에 한 번에 저장 (성능에 더 효율적)
        pointRepository.saveAll(pointsToSave);
    }
}