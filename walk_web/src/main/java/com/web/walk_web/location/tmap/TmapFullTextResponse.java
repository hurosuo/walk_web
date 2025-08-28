package com.web.walk_web.location.tmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapFullTextResponse {

    private SearchResult searchResult;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResult {
        private List<PointAddress> pointAddress;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PointAddress {
        // Tmap Full Text Geocoding 결과의 주요 필드들
        private String fullAddress; // 전체 지번주소
        private String buildingName;
        private String city_do;
        private String gu_gun;
        private String eup_myun;
        private String legalDong;
        private String adminDong;
        private Double frontLat;    // 위도
        private Double frontLon;    // 경도
    }
}
