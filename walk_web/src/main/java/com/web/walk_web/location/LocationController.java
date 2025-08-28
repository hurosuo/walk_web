package com.web.walk_web.location;

import com.web.walk_web.domain.dto.TMapDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walk/location")
public class LocationController {

    private final LocationService locationService;

    // 예: /walk/location/now?lat=37.5759&lon=127.0255
    @GetMapping("/now")
    public ResponseEntity<TMapDto> now(@RequestParam double lat,
                                       @RequestParam double lon) {
        return ResponseEntity.ok(locationService.getNow(lat, lon));
    }

    // 예: /walk/location/search?query=회기역&limit=3
    @GetMapping("/search")
    public ResponseEntity<List<TMapDto>> search(@RequestParam String query,
                                                @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(locationService.search(query, limit));
    }
}
