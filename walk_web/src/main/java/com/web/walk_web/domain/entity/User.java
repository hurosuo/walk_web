package com.web.walk_web.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자의 접근을 PROTECTED로 제한
@Table(name = "User") // 데이터베이스의 'User' 테이블과 매핑

public class User {

    public enum UserGender {
        male, female, other
    }
    // 일단은 원하는 배경은 도시랑 정원만 설정함.
    public enum WantedEnvironment {
        CITY, PARK
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL의 AUTO_INCREMENT와 동일
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_password", nullable = false, length = 100)
    private String password;

    @Column(name = "user_nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "user_age")
    private Integer age;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열 자체로 저장
    @Column(name = "user_gender")
    private UserGender gender;

    @CreationTimestamp // 엔티티가 생성될 때 현재 시간을 자동으로 저장
    @Column(name = "user_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_want_env")
    private WantedEnvironment wantEnv;

    @Column(name = "user_is_pet")
    private Boolean isPet;

    @Builder
    public User(String password, String nickname, Integer age, UserGender gender, WantedEnvironment wantEnv, Boolean isPet) {
        this.password = password;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.wantEnv = wantEnv;
        this.isPet = isPet;
    }
}