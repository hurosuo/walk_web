package com.web.walk_web.user;

import com.web.walk_web.domain.entity.User;
import com.web.walk_web.domain.dto.UserDto;
import com.web.walk_web.domain.dto.UserStatsDto;
import com.web.walk_web.myroute.MyRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Security import 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service

@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MyRouteRepository myRouteRepository;

    @Transactional
    public User signup(UserDto.SignUpRequest requestDto) {
        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
// DTO를 Entity로 변환할 때 암호화된 비밀번호를 사용
        User user = requestDto.toEntity(passwordEncoder.encode(requestDto.getPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(UserDto.LoginRequest requestDto) {
        User user = userRepository.findByNickname(requestDto.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 닉네임입니다."));
// 평문 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserStatsDto getUserStats(Long userId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        Long monthlyWalks = myRouteRepository.countMonthlyWalks(userId, startOfMonth, endOfMonth);
        Long totalWalks = myRouteRepository.countTotalWalks(userId);
        Double totalDistance = myRouteRepository.sumTotalDistance(userId);
        Long totalSeconds = myRouteRepository.sumTotalDurationInSeconds(userId);
        monthlyWalks = (monthlyWalks == null) ? 0L : monthlyWalks;
        totalWalks = (totalWalks == null) ? 0L : totalWalks;
        totalDistance = (totalDistance == null) ? 0.0 : totalDistance;

        long totalHours = (totalSeconds == null) ? 0L : (totalSeconds) / 3600;

        return new UserStatsDto(monthlyWalks, totalWalks, totalDistance, totalHours);

    }

}