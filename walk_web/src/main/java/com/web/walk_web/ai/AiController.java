package com.web.walk_web.ai;

import com.web.walk_web.domain.dto.InfoDto;
import com.web.walk_web.domain.entity.AiRouteRecommend;
import com.web.walk_web.ai.AiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walk/ai")
public class AiController {

    private final AiService agentService;
    private final AiRepository repo;

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody InfoDto info) {
        List<AiRouteRecommend> routes = agentService.invokeAgentAndParse(info);
        List<AiRouteRecommend> saved = repo.saveAll(routes);
        return ResponseEntity.ok(saved.stream().map(AiRouteRecommend::getId).toList());
    }
}
