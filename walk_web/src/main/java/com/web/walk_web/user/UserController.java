package com.web.walk_web.user;

import com.web.walk_web.domain.entity.User;
import com.web.walk_web.domain.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserDto.UserResponse> login(@RequestBody UserDto.LoginRequest requestDto, HttpSession session) {
        User loginUser = userService.login(requestDto);
        session.setAttribute("loginUser", new UserDto.UserResponse(loginUser));
        return ResponseEntity.ok(new UserDto.UserResponse(loginUser));
    }


    //   @GetMapping("/logout") 부분은 Spring Security가 대신 처리하므로 삭제.

}