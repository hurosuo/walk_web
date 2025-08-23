package com.web.walk_web.user;

import com.web.walk_web.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 닉네임으로 사용자를 찾는 메소드 (로그인 시 사용)
    Optional<User> findByNickname(String nickname);
}