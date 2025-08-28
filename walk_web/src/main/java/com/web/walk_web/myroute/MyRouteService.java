package com.web.walk_web.user.myroute;

import com.web.walk_web.domain.entity.MyRoute;
import com.web.walk_web.domain.dto.MyRouteDto;
import com.web.walk_web.user.myroute.MyRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; // List import
import java.util.stream.Collectors; // Collectors import

@Service
@RequiredArgsConstructor
public class MyRouteService {

    private final MyRouteRepository myRouteRepository;

    @Transactional(readOnly = true)
    // 반환 타입을 Page<MyRouteDto>에서 List<MyRouteDto>로 변경
    // page, size 파라미터 제거
    public List<MyRouteDto> findMyRoutes(Long userId, String sortType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "route.createdAt");

        if ("frequent".equals(sortType)) {
            sort = Sort.by(Sort.Direction.DESC, "walkCount");
        } else if ("rating".equals(sortType)) {
            sort = Sort.by(Sort.Direction.DESC, "rating");
        } else if ("favorite".equals(sortType)) {
            sort = Sort.by(Sort.Direction.DESC, "isFavorite");
        }

        // Repository 호출 시 Sort 객체만 전달
        List<MyRoute> entityList = myRouteRepository.findByUser_Id(userId, sort);

        // List<Entity>를 List<DTO>로 변환하여 반환
        return entityList.stream()
                .map(MyRouteDto::new)
                .collect(Collectors.toList());
    }
}