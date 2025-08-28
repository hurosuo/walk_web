package com.web.walk_web;

import com.web.walk_web.domain.dto.WeatherDto;
import com.web.walk_web.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/walk")
@RestController
@RequiredArgsConstructor
public class RootController {

    private final WeatherService weatherService;

    // 예: GET http://52.3.42.186/walk?nx=61&ny=127
    @GetMapping
    public ResponseEntity<WeatherDto> getUltraShortForecast(
            @RequestParam(required = false, defaultValue = "61") int nx,
            @RequestParam(required = false, defaultValue = "127") int ny
    ) {
        WeatherDto dto = weatherService.getUltraShortForecast(nx, ny);
        return ResponseEntity.ok(dto); // JSON 으로 반환됨
    }


}
