package com.web.walk_web.location;

import com.web.walk_web.domain.dto.InfoDto;
import com.web.walk_web.domain.dto.LocationDto;
import com.web.walk_web.domain.entity.AiRouteRecommend;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walk/location")
public class LocationController {

    private final LocationService locationService;

    // 현재 위치: lat, lon은 프론트에서 전달
    @GetMapping("/now")
    public ResponseEntity<?> getNowLocation(@RequestParam double lat,
                                            @RequestParam double lon) {
        LocationDto dto = locationService.getNowLocation(lat, lon);
        return dto.isDongdaemun() ? ResponseEntity.ok(dto) : ResponseEntity.ok(false);
    }

    // 주소 검색: 비슷한 주소 3개. 동대문구 아니면 false
    @GetMapping("/search")
    public ResponseEntity<?> searchLocation(@RequestParam String query) {
        List<LocationDto> list = locationService.searchLocation(query);

        // 동대문구만 필터링
        List<LocationDto> ddmOnly = list.stream()
                .filter(LocationDto::isDongdaemun)
                .toList();

        if (ddmOnly.isEmpty()) return ResponseEntity.ok(false);
        return ResponseEntity.ok(ddmOnly);
    }

    @PostMapping("/now")
    public ResponseEntity<InfoDto> handleLocation(@RequestBody LocationDto locationDto) {
        // 예: 유저가 선택한 조건 (나중에 파라미터/세션에서 받아올 수도 있음)
        InfoDto infoDto = InfoDto.fromLocationDto(
                locationDto,
                InfoDto.RouteDuration.MIN_30,
                InfoDto.RoutePurpose.CITY,
                false
        );

        return ResponseEntity.ok(infoDto);
    }
}
