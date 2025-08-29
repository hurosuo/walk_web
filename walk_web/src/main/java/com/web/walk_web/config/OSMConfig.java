// OSMConfig.java
package com.web.walk_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OSMConfig {

    @Bean
    public WebClient osmWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "walk-web/1.0 (contact: youremail@example.com)")
                .defaultHeader("Accept-Language", "ko") // 한국어 선호
                .build();
    }
}
