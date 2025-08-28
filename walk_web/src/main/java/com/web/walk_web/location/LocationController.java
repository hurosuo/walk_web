package com.web.walk_web.location;

import com.web.walk_web.domain.dto.LocationDto;
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
}
