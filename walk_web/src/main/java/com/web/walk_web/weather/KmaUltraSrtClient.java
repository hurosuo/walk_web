package com.web.walk_web.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class KmaUltraSrtClient {

    private static final String ENDPOINT =
            "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    // 환경변수/설정값로도 받을 수 있게 (없으면 기본값으로 네 키 사용)
    @Value("${kma.service-key:ce06060f9f62ccf600750c68416d338bc0434d96e86607dc1ccdbce79d07c177}")
    private String serviceKeyRaw;

    public JsonNode call(String baseDate, String baseTime, int nx, int ny) {
        try {
            String key = URLEncoder.encode(serviceKeyRaw, StandardCharsets.UTF_8);
            String q = String.format(
                    "serviceKey=%s&numOfRows=%d&pageNo=%d&dataType=JSON&base_date=%s&base_time=%s&nx=%d&ny=%d",
                    key, 1000, 1, baseDate, baseTime, nx, ny
            );
            String url = ENDPOINT + "?" + q;

            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                throw new IllegalStateException("KMA API HTTP " + res.statusCode());
            }

            JsonNode root = mapper.readTree(res.body());
            JsonNode items = root.path("response").path("body").path("items").path("item");
            if (!items.isArray()) {
                throw new IllegalStateException("KMA API parse error: items not array");
            }
            return items;
        } catch (Exception e) {
            throw new RuntimeException("KMA call failed: " + e.getMessage(), e);
        }
    }
}
