package com.web.walk_web.location;

import com.web.walk_web.domain.dto.InfoDto;
import com.web.walk_web.domain.dto.LocationDto;
import com.web.walk_web.domain.entity.AiRouteRecommend;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/search")
    public LocationDto search(
            @RequestParam(value = "jibunAddress", required = false) String jibunAddress,
            @RequestParam(value = "query", required = false) String query
    ) {
        String raw = (jibunAddress != null && !jibunAddress.isBlank()) ? jibunAddress : query;
        if (raw == null || raw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jibunAddress or query is required");
        }
        return locationService.searchByAddress(stripQuotes(raw));
    }

    private String stripQuotes(String s) {
        String v = s.strip();
        if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("“") && v.endsWith("”"))) {
            v = v.substring(1, v.length() - 1).strip();
        }
        return v;
    }
}
