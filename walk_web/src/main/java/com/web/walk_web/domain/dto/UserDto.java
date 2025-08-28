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
        private Boolean isPet;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .password(encodedPassword) // 암호화된 비밀번호를 저장
                    .nickname(this.nickname)
                    .age(this.age)
                    .gender(this.gender)
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

        // Entity를 DTO로 변환하는 생성자
        public UserResponse(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.age = user.getAge();
            this.gender = user.getGender();
        }
    }
    @Getter
    public static class LoginResponse {
        private Long id;
        private String email;
        private String nickname;
        private String sessionId; // 프론트에서 요청한 세션 ID

        public LoginResponse(User user, String sessionId) {
            this.id = user.getId();
            this.email = user.getNickname(); // User 엔티티에 getEmail()이 있다고 가정
            this.nickname = user.getNickname();
            this.sessionId = sessionId;
        }
    }

}