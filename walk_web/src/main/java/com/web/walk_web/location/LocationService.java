package com.web.walk_web.location;

import com.web.walk_web.domain.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final WebClient tmapGeoWebClient; // TmapConfig에서 주입

    /**
     * 현재 위경도 -> 지번주소
     */
    public LocationDto getNowLocation(double lat, double lon) {
        String uri = UriComponentsBuilder.fromPath("/tmap/geo/reversegeocoding")
                .queryParam("version", 1)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("coordType", "WGS84GEO")
                .queryParam("addressType", "A10") // 지번주소
                .toUriString();

        Map<String, Object> res = safeGet(uri);
        Map<String, Object> addr = res == null ? null : (Map<String, Object>) res.get("addressInfo");

        String jibun = addr == null ? null : asString(addr.get("fullAddress"));
        boolean isDDM = jibun != null && jibun.contains("동대문구");

        return new LocationDto(jibun, lat, lon, isDDM);
    }

    /**
     * 검색어 -> 지번주소 후보 (동대문구만, 최대 3건)
     */
    public List<LocationDto> searchLocation(String query) {
        // ❗ Tmap fullAddrGeo는 파라미터 이름이 'address' 임. (fullAddr 아님)
        String uri = UriComponentsBuilder.fromPath("/tmap/geo/fullAddrGeo")
                .queryParam("version", 1)
                .queryParam("coordType", "WGS84GEO")
                .queryParam("addressFlag", "F02")     // 지번/도로명 모두 포함 (우린 지번만 사용)
                .queryParam("address", query)         // <-- 핵심 수정
                .queryParam("count", 20)
                .toUriString();

        Map<String, Object> res = safeGet(uri);
        Map<String, Object> coordInfo = res == null ? null : (Map<String, Object>) res.get("coordinateInfo");
        Object coord = coordInfo == null ? null : coordInfo.get("coordinate");

        List<Map<String, Object>> rows;
        if (coord instanceof List<?> list) {
            rows = ((List<?>) list).stream()
                    .filter(Objects::nonNull)
                    .map(o -> (Map<String, Object>) o)
                    .toList();
        } else if (coord instanceof Map<?, ?> single) {
            rows = List.of((Map<String, Object>) single);
        } else {
            rows = List.of();
        }

        List<LocationDto> result = new ArrayList<>(3);
        Set<String> seenJibun = new LinkedHashSet<>();

        for (Map<String, Object> c : rows) {
            // 1) 지번 주소
            String jibun = asString(c.get("fullAddress")); // 없을 수도 있어 방어
            if (jibun == null || jibun.isBlank()) {
                String si    = asString(c.get("city_do"));
                String gu    = asString(c.get("gu_gun"));
                String dong  = asString(c.get("eup_myun"));
                String bunji = asString(c.get("bunji"));
                jibun = joinSpace(si, gu, dong, bunji);
                if (jibun.isBlank()) continue;
            }

            // 2) 좌표: newLat/newLon 우선
            String latStr = firstNonNull(asString(c.get("newLat")), asString(c.get("lat")));
            String lonStr = firstNonNull(asString(c.get("newLon")), asString(c.get("lon")));
            if (latStr == null || lonStr == null) continue;

            double lat, lon;
            try {
                lat = Double.parseDouble(latStr);
                lon = Double.parseDouble(lonStr);
            } catch (NumberFormatException e) {
                continue;
            }

            // 3) 동대문구 필터 (gu_gun 우선, 보조로 문자열 포함)
            String gu = asString(c.get("gu_gun"));
            boolean isDDM = (gu != null && gu.contains("동대문구")) || jibun.contains("동대문구");
            if (!isDDM) continue;

            // 4) 중복 지번 제거
            if (!seenJibun.add(jibun)) continue;

            result.add(new LocationDto(jibun, lat, lon, true));
            if (result.size() == 3) break;
        }

        return result;
    }

    /* ----------------- 내부 유틸 ----------------- */

    private Map<String, Object> safeGet(String uri) {
        try {
            return tmapGeoWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            // 필요하면 로그 추가
            return null;
        }
    }

    private static String asString(Object o) { return o == null ? null : String.valueOf(o); }

    private static String firstNonNull(String a, String b) { return (a != null && !a.isBlank()) ? a : b; }

    private static String joinSpace(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isBlank()) {
                if (!sb.isEmpty()) sb.append(' ');
                sb.append(p.trim());
            }
        }
        return sb.toString();
    }
}
