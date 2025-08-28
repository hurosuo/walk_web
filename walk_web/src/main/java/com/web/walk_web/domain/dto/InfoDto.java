package com.web.walk_web.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoDto {

    private RouteDuration duration; // 15분, 30분, 45분, 60분 중 하나
    private RoutePurpose purpose;   // 도심, 야경, 조용, 경치 중 하나

    private String addressJibun;
    private double latitude;     // 위도
    private double longitude;    // 경도
    private boolean withPet; // 기본값 false

    // 내부 enum - Duration
    public enum RouteDuration {
        MIN_15, // 15분
        MIN_30, // 30분
        MIN_45, // 45분
        MIN_60  // 60분
    }

    // 내부 enum - Purpose
    public enum RoutePurpose {
        CITY,       // 도심
        NIGHT_VIEW, // 야경
        QUIET,      // 조용
        SCENERY     // 경치
    }

    // LocationDto → InfoDto 변환 메서드
    public static InfoDto fromLocationDto(LocationDto locationDto,
                                          RouteDuration duration,
                                          RoutePurpose purpose,
                                          boolean withPet) {
        return InfoDto.builder()
                .duration(duration)
                .purpose(purpose)
                .addressJibun(locationDto.getJibunAddress())
                .latitude(locationDto.getLatitude())
                .longitude(locationDto.getLongitude())
                .withPet(withPet)
                .build();
    }
}
