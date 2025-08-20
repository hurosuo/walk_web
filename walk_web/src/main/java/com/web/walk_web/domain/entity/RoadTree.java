package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "RoadTree")
public class RoadTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "road_tree_id")
    private Long id;
    //중요!!!  원래 회의에서 도로폭/보도폭/ 식재간격의 데이터는 받게될 데이터 형식에 없음
    //따라서 만약 그 데이터가 필요하다면 흉고직경을 기반으로 추측해야할듯.
    @Column(name = "management_number", unique = true)
    private String managementNumber; // 관리번호 (데이터 중복 저장을 막기 위함)

    @Column(name = "tree_name", nullable = false)
    private String treeName; // 수목명

    @Column(name = "road_address")
    private String roadAddress; // 도로명주소

    @Column(name = "latitude", nullable = false)
    private Double latitude; // 수목 위도

    @Column(name = "longitude", nullable = false)
    private Double longitude; // 수목 경도

    @Column(name = "dbh")
    private Integer dbh; // 흉고직경 (Diameter at Breast Height)

    @Column(name = "road_start_point")
    private String roadStartPoint; // 도로 시작점

    @Column(name = "road_end_point")
    private String roadEndPoint; // 도로 종료점

    @Builder
    public RoadTree(String managementNumber, String treeName, String roadAddress, Double latitude, Double longitude, Integer dbh, String roadStartPoint, String roadEndPoint) {
        this.managementNumber = managementNumber;
        this.treeName = treeName;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dbh = dbh;
        this.roadStartPoint = roadStartPoint;
        this.roadEndPoint = roadEndPoint;
    }
}