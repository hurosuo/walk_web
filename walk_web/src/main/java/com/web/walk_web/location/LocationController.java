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

    // 지번 주소 -> 위도, 경도로 받아서 json으로 반환하는 형태
    @GetMapping("/search")
    public ResponseEntity<LocationDto> search(@RequestParam String jibunAddress) {
        LocationDto dto = locationService.searchByJibun(jibunAddress);
        return ResponseEntity.ok(dto);
    }
}
