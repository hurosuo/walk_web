package com.web.walk_web.location.tmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapReverseResponse {
    private AddressInfo addressInfo;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressInfo {
        private String fullAddress; // 전체 주소(지번 포함)
        private String gu_gun;      // 구/군명
        private String city_do;     // 시/도
        private String legalDong;   // 법정동
        private String adminDong;   // 행정동
        private Double lon;
        private Double lat;
    }
}
