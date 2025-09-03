// src/main/java/com/web/walk_web/location/LocationService.java
package com.web.walk_web.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.walk_web.domain.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class LocationService {

    // 여기부터 servive(지번 주소->위도,경도) 구역
    private final WebClient tmapWebClient;
    private final ObjectMapper om = new ObjectMapper();

    public LocationDto searchByJibun(String jibunAddress) {

        if (!StringUtils.hasText(jibunAddress)) {
            throw new IllegalArgumentException("jibunAddress is required");
        }

        // Tmap full address geocoding
        // 문서 기준 주요 파라미터: version=1, format=json, coordType=WGS84GEO, fullAddr={주소}
        Mono<String> respMono = tmapWebClient.get()
                .uri(builder -> buildFullAddrGeoUri(builder, jibunAddress))
                .retrieve()
                .bodyToMono(String.class);

        String body = respMono.block(); // 간단 구현: 동기(block). 필요시 비동기로 리팩터 가능.
        if (!StringUtils.hasText(body)) {
            throw new RuntimeException("Empty response from Tmap");
        }

        try {
            JsonNode root = om.readTree(body);

            // 응답 구조 예: { "coordinateInfo": { "coordinate": [ { "lat": "...", "lon": "..." , ... } ] } }
            JsonNode coordInfo = root.path("coordinateInfo");
            JsonNode coordinates = coordInfo.path("coordinate");

            if (!coordinates.isArray() || coordinates.isEmpty()) {
                throw new RuntimeException("No coordinates found in Tmap response");
            }

            JsonNode first = coordinates.get(0);

            // lat/lon 필드가 케이스에 따라 newLat/newLon 또는 lat/lon일 수 있으니 모두 대응
            double lat = pickDouble(first, "lat", "newLat", "latEntr", "frontLat");
            double lon = pickDouble(first, "lon", "newLon", "lonEntr", "frontLon");

            // 동대문구 여부: 요청 주소 기준 + 응답의 행정구(gu_gun, adminDong 등)에서도 보조 체크
            boolean isDongdaemun = containsDongdaemun(jibunAddress)
                    || containsDongdaemun(first.path("adminDong").asText(null))
                    || containsDongdaemun(first.path("legalDong").asText(null))
                    || containsDongdaemun(first.path("gu_gun").asText(null));

            return new LocationDto(jibunAddress, lat, lon, isDongdaemun);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Tmap response: " + e.getMessage(), e);
        }
    }

    private boolean containsDongdaemun(String text) {
        if (!StringUtils.hasText(text)) return false;
        return text.contains("동대문구");
    }

    private double pickDouble(JsonNode obj, String... candidateNames) {
        for (String name : candidateNames) {
            JsonNode n = obj.get(name);
            if (n != null && n.isTextual() && n.asText().length() > 0) {
                try {
                    return Double.parseDouble(n.asText());
                } catch (NumberFormatException ignored) {}
            } else if (n != null && n.isNumber()) {
                return n.asDouble();
            }
        }
        throw new IllegalStateException("No numeric value found for fields: " + String.join(",", candidateNames));
    }

    private java.net.URI buildFullAddrGeoUri(UriBuilder b, String jibunAddress) {
        String encoded = URLEncoder.encode(jibunAddress, StandardCharsets.UTF_8);
        return b.path("/tmap/geo/fullAddrGeo")
                .queryParam("version", 1)
                .queryParam("format", "json")
                .queryParam("coordType", "WGS84GEO")
                .queryParam("fullAddr", encoded)
                .build();
    }
}
