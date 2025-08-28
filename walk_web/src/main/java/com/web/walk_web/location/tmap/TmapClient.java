package com.web.walk_web.location.tmap;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TmapClient {

    private final WebClient tmapWebClient;

    @Value("${tmap.version:1}")
    private int version;

    @Value("${tmap.default-coord-type:WGS84GEO}")
    private String coordType;

    // Reverse Geocoding: 좌표 -> 주소
    public Mono<TmapReverseResponse> reverseGeocode(double lat, double lon) {
        String uri = UriComponentsBuilder.fromPath("/tmap/geo/reversegeocoding")
                .queryParam("version", version)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("coordType", coordType)
                // addressType: A10(지번), A01(도로명) 등. 지번을 우선 사용.
                .queryParam("addressType", "A10")
                .build()
                .toUriString();

        return tmapWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(TmapReverseResponse.class);
    }

    // Full Text Geocoding: 텍스트 -> 주소/좌표
    public Mono<TmapFullTextResponse> fullTextGeocode(String query) {
        String uri = UriComponentsBuilder.fromPath("/tmap/geo/fullAddrGeo")
                .queryParam("version", version)
                .queryParam("coordType", coordType)
                .queryParam("fullAddr", query)
                .build()
                .toUriString();

        return tmapWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(TmapFullTextResponse.class);
    }
}
