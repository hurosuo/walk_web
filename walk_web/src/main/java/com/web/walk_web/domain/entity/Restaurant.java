package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // 업소명

    @Column(name = "category")
    private String category; // 업태명 (한식, 중식, 카페 등)

    @Column(name = "road_address")
    private String roadAddress; // 소재지(도로명)

    @Builder
    public Restaurant(String name, String category, String roadAddress) {
        this.name = name;
        this.category = category;
        this.roadAddress = roadAddress;
    }
}