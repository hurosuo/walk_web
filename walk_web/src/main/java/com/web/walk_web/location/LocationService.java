package com.web.walk_web.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final WebClient osmWebClient;
    private final ObjectMapper om = new ObjectMapper();

    public LocationDto searchByAddress(String input) {
        try {
            // 1) OSM 지오코딩
            String json = osmWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("format", "jsonv2")
                            .queryParam("addressdetails", "1")
                            .queryParam("limit", "1")
                            .queryParam("q", input)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode arr = om.readTree(json);
            if (arr == null || !arr.isArray() || arr.isEmpty()) {
                // 못 찾으면 입력값 그대로 지번주소로 돌려주되, 좌표 0,0
                return new LocationDto(input, 0.0, 0.0, containsDongdaemun(input));
            }

            JsonNode first = arr.get(0);
            double lat = parseDouble(first.path("lat").asText(null));
            double lon = parseDouble(first.path("lon").asText(null));
            JsonNode addr = first.path("address");

            // 2) 지번주소(jibunAddress) 보정 로직
            // - 입력이 이미 지번형이면 그대로 사용
            // - 아니면 OSM address 컴포넌트로 "시/도 + 구 + 동 + 번지" 형태 조합
            String jibun = looksLikeJibun(input) ? normalizeSpaces(input)
                    : composeJibun(addr, first.path("display_name").asText(""));

            // 3) 동대문구 여부 (주소 컴포넌트 기반, 마지막 보조로 문자열 포함)
            boolean isDongdaemun = isDongdaemunAddr(addr) || containsDongdaemun(jibun);

            return new LocationDto(jibun, lat, lon, isDongdaemun);

        } catch (Exception e) {
            return new LocationDto(input, 0.0, 0.0, containsDongdaemun(input));
        }
    }

    private boolean isDongdaemunAddr(JsonNode address) {
        if (address == null || address.isMissingNode()) return false;
        String[] keys = {"borough", "city_district", "district", "county", "state_district"};
        for (String k : keys) {
            String v = address.path(k).asText("");
            if (!v.isBlank() && v.contains("동대문구")) return true;
        }
        return false;
    }

    private boolean containsDongdaemun(String s) {
        return s != null && s.contains("동대문구");
    }

    private double parseDouble(String s) {
        if (s == null) return 0.0;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0.0; }
    }

    private boolean looksLikeJibun(String s) {
        if (s == null) return false;
        // 대략적인 지번 패턴: "... 동 39-9" 또는 "... 동 39"
        return s.matches(".*[가-힣]+동\\s*\\d+(?:-\\d+)?(\\s|$)");
    }

    private String normalizeSpaces(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private String composeJibun(JsonNode addr, String display) {
        // OSM의 address 컴포넌트에서 최대한 지번형에 가깝게 조합
        String sido = pick(addr, "state", "region");             // 서울특별시
        String gugu = pick(addr, "borough", "city_district", "district", "county"); // 동대문구
        String dong = pick(addr, "suburb", "neighbourhood", "quarter", "hamlet");   // 용두동/신설동 등
        String beonji = pick(addr, "house_number");              // 39-9 등 (OSM에선 번지 대신 house_number로 들어옴)

        // 조합 우선순위: 시/도 + 구 + 동 + 번지
        StringBuilder sb = new StringBuilder();
        if (!sido.isBlank()) sb.append(sido).append(" ");
        if (!gugu.isBlank()) sb.append(gugu).append(" ");
        if (!dong.isBlank()) sb.append(dong).append(" ");
        if (!beonji.isBlank()) sb.append(beonji);

        String candidate = normalizeSpaces(sb.toString());
        if (!candidate.isBlank()) return candidate;

        // 그래도 못 만들면 display_name 반환
        return normalizeSpaces(display);
    }

    private String pick(JsonNode node, String... keys) {
        if (node == null || node.isMissingNode()) return "";
        for (String k : keys) {
            String v = node.path(k).asText("");
            if (!v.isBlank()) return v;
        }
        return "";
    }
}
