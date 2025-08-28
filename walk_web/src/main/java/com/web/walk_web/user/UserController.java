package com.web.walk_web.user;

import com.web.walk_web.domain.entity.User;
import com.web.walk_web.domain.dto.UserDto;
import com.web.walk_web.domain.dto.UserStatsDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping("/walk/users")

public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto.UserResponse> signup(@RequestBody UserDto.SignUpRequest requestDto) {
        User savedUser = userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto.UserResponse(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto.LoginRequest requestDto, HttpServletRequest request) {
        try {
// 1. Service에서 로그인 검증
            User user = userService.login(requestDto);
// 2. 로그인 성공 시, 세션 생성 (이미 있으면 기존 세션 반환)
            HttpSession session = request.getSession(true); // true: 세션이 없으면 새로 생성
// 3. 세션에 사용자 정보 저장 (전체 User 객체보다는 id나 닉네임 등 최소 정보 권장)
            session.setAttribute("loginUser", user.getId()); // 예: "loginUser"라는 키로 유저 ID 저장
            session.setMaxInactiveInterval(1800); // 세션 유효 시간 설정 (초 단위, 예: 30분)
// 4. 프론트엔드에 전달할 응답 DTO 생성
            UserDto.LoginResponse responseDto = new UserDto.LoginResponse(user, session.getId());
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
// 5. 로그인 실패 시, 에러 메시지 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

        UserStatsDto stats=userService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }


}


