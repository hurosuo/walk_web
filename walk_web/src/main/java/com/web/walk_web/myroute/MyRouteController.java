package com.web.walk_web.myroute;


import com.web.walk_web.domain.dto.MyRouteDto;
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


    @GetMapping("/{my_route_id}")
    public ResponseEntity<?> getMyRouteById(
            // ✅ 2. @PathVariable이 경로의 my_route_id 값을 인식하도록 설정
            @PathVariable("my_route_id") Long myRouteId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // ✅ 3. 변경된 변수 이름을 Service에 전달
        MyRouteDto myRoute = myRouteService.findMyRouteById(userId, myRouteId);

        return ResponseEntity.ok(myRoute);
    }

    @GetMapping
    public ResponseEntity<?> getMyRoutes(
            HttpSession session,
            @RequestParam(defaultValue = "recent") String sort,
            // ✅ 2. 'isFavorite' 필터 파라미터 추가
            // required = false: 이 파라미터가 없어도 에러가 나지 않음
            // Boolean(객체타입): 파라미터가 없을 때 null 값을 가질 수 있도록 함
            @RequestParam(required = false) Boolean isFavorite
    ) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // Service에 isFavorite 값을 그대로 전달
        List<MyRouteDto> myRoutes = myRouteService.findMyRoutes(userId, sort, isFavorite);

        return ResponseEntity.ok(myRoutes);
    }

    @PatchMapping("/{my_route_id}/favorite")
    public ResponseEntity<Void> updateFavoriteStatus(
            @PathVariable("my_route_id") Long myRouteId,
            @RequestBody MyRouteDto.UpdateFavoriteRequest requestDto,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) {
            // 권한이 없으면 401 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 서비스 계층에 필요한 정보(userId, myRouteId, 변경할 값)를 모두 전달
        myRouteService.updateFavoriteStatus(userId, myRouteId, requestDto.isFavorite());

        // 성공적으로 수정되었지만 별도의 응답 본문은 없다는 의미의 204 No Content 반환
        return ResponseEntity.noContent().build();
    }

}