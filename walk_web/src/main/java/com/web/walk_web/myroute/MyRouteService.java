package com.web.walk_web.myroute;

import com.web.walk_web.domain.entity.MyRoute;
import com.web.walk_web.domain.dto.MyRouteDto;
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

    @Transactional(readOnly = true)
    // ✅ 파라미터 변수 이름을 myRouteId로 변경 (통일성)
    public MyRouteDto findMyRouteById(Long userId, Long myRouteId) {
        // ✅ Repository 호출 시에도 변경된 변수 전달
        MyRoute myRoute = myRouteRepository.findByUser_IdAndId(userId, myRouteId)
                .orElseThrow(() -> new IllegalArgumentException("경로를 찾을 수 없거나 접근 권한이 없습니다."));

        return new MyRouteDto(myRoute);
    }

    @Transactional(readOnly = true)
    // ✅ 2. isFavorite 파라미터를 받도록 메소드 시그니처 수정
    public List<MyRouteDto> findMyRoutes(Long userId, String sortType, Boolean isFavorite) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id"); // createdAt 대신 id로 변경 (가정)
        // ... (정렬 로직은 동일) ...

        List<MyRoute> entityList;

        // isFavorite 파라미터가 true일 경우, 즐겨찾기된 경로만 조회
        if (isFavorite != null && isFavorite) {
            entityList = myRouteRepository.findByUser_IdAndIsFavorite(userId, true, sort);
        } else {
            // 파라미터가 없거나 false일 경우, 모든 경로 조회
            entityList = myRouteRepository.findByUser_Id(userId, sort);
        }

        return entityList.stream()
                .map(MyRouteDto::new)
                .collect(Collectors.toList());
    }

    @Transactional // 이 어노테이션 덕분에 메소드 종료 시 변경 사항이 DB에 자동 저장됨
    public void updateFavoriteStatus(Long userId, Long myRouteId, boolean isFavorite) {
        // 1. 로그인한 사용자의 경로가 맞는지 확인하며 경로를 찾아옴
        MyRoute myRoute = myRouteRepository.findByUser_IdAndId(userId, myRouteId)
                .orElseThrow(() -> new IllegalArgumentException("경로를 찾을 수 없거나 접근 권한이 없습니다."));

        // 2. 엔티티의 상태를 변경
        myRoute.updateFavorite(isFavorite);

        // 3. @Transactional에 의해 메소드가 끝나면 자동으로 DB에 update 쿼리가 실행됨
    }
}