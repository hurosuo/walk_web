package com.web.walk_web.domain.dto;

import com.web.walk_web.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignUpRequest {
        private String password;
        private String nickname;
        private Integer age;
        private User.UserGender gender;
        private User.WantedEnvironment wantEnv;
        private Boolean isPet;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .password(encodedPassword) // 암호화된 비밀번호를 저장
                    .nickname(this.nickname)
                    .age(this.age)
                    .gender(this.gender)
                    .wantEnv(this.wantEnv)
                    .isPet(this.isPet)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequest {
        private String nickname;
        private String password;
    }

    @Getter
    public static class UserResponse {
        private final Long id;
        private final String nickname;
        private final Integer age;
        private final User.UserGender gender;
        private final User.WantedEnvironment wantEnv;
        private final Boolean isPet;

        // Entity를 DTO로 변환하는 생성자
        public UserResponse(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.age = user.getAge();
            this.gender = user.getGender();
            this.wantEnv = user.getWantEnv();
            this.isPet = user.getIsPet();
        }
    }
}