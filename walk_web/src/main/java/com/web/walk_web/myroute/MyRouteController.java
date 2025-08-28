package com.web.walk_web.user.myroute;


import com.web.walk_web.domain.dto.MyRouteDto;
import com.web.walk_web.user.myroute.MyRouteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; // List import

@RestController
@RequiredArgsConstructor
@RequestMapping("/walk/my-routes")
public class MyRouteController {

    private final MyRouteService myRouteService;

    @GetMapping
    // 반환 타입을 ResponseEntity<Page<MyRouteDto>>에서 ResponseEntity<?>로 변경 (List도 받을 수 있도록)
    // page, size 파라미터 제거
    public ResponseEntity<?> getMyRoutes(
            HttpSession session,
            @RequestParam(defaultValue = "recent") String sort
    ) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // Service 호출 결과로 List<MyRouteDto>를 받음
        List<MyRouteDto> myRoutes = myRouteService.findMyRoutes(userId, sort);

        return ResponseEntity.ok(myRoutes);
    }
}