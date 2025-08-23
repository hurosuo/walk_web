package com.web.walk_web.config;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 모든 Controller에서 발생하는 예외를 처리하는 클래스
@RestControllerAdvice
public class GlobalExceptionHandler {

    // IllegalArgumentException이 발생했을 때 이 메소드가 실행됨
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 예외 메시지를 Response Body에 담아 400 Bad Request 상태 코드와 함께 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}