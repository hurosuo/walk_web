package com.web.walk_web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class TmapConfig {

    @Value("${tmap.base-url:https://apis.openapi.sk.com}")
    private String baseUrl;

    @Value("${tmap.app-key:vxS8dcTsz26EHfbwHI2HSfMf36LSUZQ71a1DKMs5}")
    private String appKey;

    @Bean
    public WebClient tmapWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("appKey", appKey)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                        .build())
                .build();
    }
}
