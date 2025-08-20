package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cctv")
public class Cctv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cctv_id")
    private Long id;

    @Column(name = "latitude", nullable = false)
    private Double latitude; // 위도 (WGSXPT)

    @Column(name = "longitude", nullable = false)
    private Double longitude; // 경도 (WGSYPT)

    @Builder
    public Cctv(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}