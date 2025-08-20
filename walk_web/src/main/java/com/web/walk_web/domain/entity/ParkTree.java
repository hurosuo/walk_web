package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ParkTree")
public class ParkTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "park_tree_id")
    private Long id;

    @Column(name = "tree_name", nullable = false)
    private String treeName; // 수목명

    @Column(name = "park_name")
    private String parkName; // 공원명

    @Column(name = "jibun_address")
    private String jibunAddress; // 지번주소

    @Column(name = "latitude", nullable = false)
    private Double latitude; // 수목 위도

    @Column(name = "longitude", nullable = false)
    private Double longitude; // 수목 경도

    @Builder
    public ParkTree(String treeName, String parkName, String jibunAddress, Double latitude, Double longitude) {
        this.treeName = treeName;
        this.parkName = parkName;
        this.jibunAddress = jibunAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}