package com.web.walk_web.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.web.walk_web.weather.KmaUltraSrtClient;
import com.web.walk_web.domain.dto.WeatherDto;
import com.web.walk_web.weather.PrecipType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final KmaUltraSrtClient client;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmm");

    public WeatherDto getUltraShortForecast(int nx, int ny) {
        // 1) 기준시각 계산 (직전 발표시각: 00/30분 라운드 → 30분 뺌)
        var now = ZonedDateTime.now(KST);
        var base = latestBase(now);

        // 2) 호출
        JsonNode items = client.call(base.date, base.time, nx, ny);

        // 3) 예보시각(fcstDate+fcstTime)으로 묶고 가장 빠른 키 하나 선택
        Map<String, List<JsonNode>> byFcst = new HashMap<>();
        for (JsonNode n : items) {
            String key = n.path("fcstDate").asText() + n.path("fcstTime").asText();
            byFcst.computeIfAbsent(key, k -> new ArrayList<>()).add(n);
        }
        if (byFcst.isEmpty()) {
            // 빈 응답 보호
            WeatherDto empty = new WeatherDto();
            empty.setBaseDateTime(base.date + " " + base.time);
            return empty;
        }
        List<String> keys = new ArrayList<>(byFcst.keySet());
        Collections.sort(keys); // 오름차순
        String chosen = keys.get(0);
        List<JsonNode> chosenItems = byFcst.get(chosen);

        // 4) 필요한 카테고리만 추출
        Map<String, String> map = new HashMap<>();
        for (JsonNode n : chosenItems) {
            String cat = n.path("category").asText();
            String val = n.path("fcstValue").asText();
            switch (cat) {
                case "T1H": // 기온(℃)
                case "REH": // 습도(%)
                case "RN1": // 1시간 강수량(mm) - "강수없음" 가능
                case "PTY": // 강수형태 코드
                case "WSD": // 풍속(m/s)
                    map.put(cat, val);
                    break;
            }
        }

        // 5) DTO 구성
        WeatherDto dto = new WeatherDto();
        dto.setBaseDateTime(base.date + " " + base.time);
        dto.setFcstDateTime(chosen.substring(0, 8) + " " + chosen.substring(8));

        if (map.containsKey("T1H")) dto.setTemperature(parseDouble(map.get("T1H")));
        if (map.containsKey("REH")) dto.setHumidity(parseInt(map.get("REH")));
        if (map.containsKey("RN1")) dto.setPrecipitationMm(parseRN1(map.get("RN1")));
        if (map.containsKey("WSD")) dto.setWindSpeed(parseDouble(map.get("WSD")));
        if (map.containsKey("PTY")) {
            Integer code = parseInt(map.get("PTY"));
            dto.setPrecipitationTypeCode(code);
            dto.setPrecipitationType(PrecipType.from(code).label());
        }

        return dto;
    }

    // ===== helpers =====

    private static class Base {
        final String date; // yyyyMMdd
        final String time; // HHmm
        Base(String d, String t) { this.date = d; this.time = t; }
    }

    private static Base latestBase(ZonedDateTime now) {
        int minute = now.getMinute();
        int baseMinute = (minute >= 30) ? 30 : 0;
        var base = now.withMinute(baseMinute).withSecond(0).withNano(0).minusMinutes(30);
        return new Base(base.format(DATE), base.format(TIME));
    }

    private static Double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return null; }
    }

    private static Integer parseInt(String s) {
        try { return (int) Math.round(Double.parseDouble(s)); } catch (Exception e) { return null; }
    }

    private static Double parseRN1(String s) {
        if (s == null) return null;
        s = s.trim();
        if ("강수없음".equals(s)) return 0.0;
        return parseDouble(s);
    }
}
