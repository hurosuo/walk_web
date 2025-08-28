package com.web.walk_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TmapConfig {

    @Value("${tmap.base-url:https://apis.openapi.sk.com}")
    private String baseUrl;

    // 실제 콘솔에서 발급한 키를 application.yml에 넣어주세요.
    @Value("${tmap.app-key:vxS8dcTsz26EHfbwHI2HSfMf36LSUZQ71a1DKMs5}")
    private String appKey;

    @Bean(name = "tmapGeoWebClient")
    public WebClient tmapGeoWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("appKey", "vxS8dcTsz26EHfbwHI2HSfMf36LSUZQ71a1DKMs5") // ❗ 하드코딩 금지: 주입된 값 사용
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                                .build()
                )
                .build();
    }
}
