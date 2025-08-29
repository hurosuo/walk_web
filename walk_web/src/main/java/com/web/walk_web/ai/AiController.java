package com.web.walk_web.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.walk_web.domain.dto.InfoDto;
import com.web.walk_web.domain.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walk/ai")
public class AiController {

    private static final Region REGION = Region.US_EAST_1;
    private static final String SHIM_LAMBDA_NAME = "action_group_springboot-ix2bg";

    private final ObjectMapper om;
    private final LambdaClient lambda = LambdaClient.builder()
            .region(REGION)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    // ⭐ 1. 직접 만든 AiResponseService를 주입받습니다.
    private final AiResponseService aiResponseService;

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody InfoDto dto) {
        try {
            byte[] payload = om.writeValueAsBytes(dto);
            var req = InvokeRequest.builder()
                    .functionName(SHIM_LAMBDA_NAME)
                    .payload(SdkBytes.fromByteArray(payload))
                    .build();

            var resp = lambda.invoke(req);
            String raw = resp.payload() == null ? "" : resp.payload().asString(StandardCharsets.UTF_8);
            String functionError = resp.functionError();

            String candidateText = raw;
            try {
                JsonNode root = om.readTree(raw);
                if (root.has("message")) {
                    candidateText = root.path("message").asText();
                }
            } catch (Exception ignore) {
            }

            String jsonStr = extractOutermostJson(candidateText);
            if (jsonStr == null) {
                jsonStr = extractOutermostJson(raw);
            }

            if (jsonStr != null) {
                ResponseDto responseDto = om.readValue(jsonStr, ResponseDto.class);

                // ⭐ 2. DTO 파싱 성공 후, 서비스를 호출하여 DB에 저장합니다.
                try {
                    aiResponseService.saveAiRoute(responseDto);
                } catch (Exception e) {
                    // DB 저장에 실패하더라도 사용자 요청은 성공 처리할 수 있도록 예외처리
                    System.err.println("AI 경로 추천 결과를 DB에 저장하는 데 실패했습니다: " + e.getMessage());
                    // 여기서 로그를 남기거나, 에러 모니터링 시스템에 알림을 보낼 수 있습니다.
                }

                return ResponseEntity.ok(responseDto);
            }

            String err = (functionError != null) ? functionError : "UnknownError";
            return ResponseEntity.internalServerError()
                    .body("{\"ok\":false,\"error\":\"" + err + "\",\"payload\":" + safeEcho(raw) + "}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"ok\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ... extractOutermostJson, safeEcho 메서드는 그대로 ...
    private String extractOutermostJson(String text) {
        if (text == null || text.isEmpty()) return null;
        boolean inString = false, escaped = false;
        int depth = 0, start = -1;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (inString) {
                if (escaped) escaped = false;
                else if (ch == '\\') escaped = true;
                else if (ch == '"') inString = false;
                continue;
            }
            if (ch == '"') {
                inString = true;
                escaped = false;
                continue;
            }
            if (ch == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (ch == '}') {
                if (depth > 0) {
                    depth--;
                    if (depth == 0 && start != -1) {
                        String candidate = text.substring(start, i + 1).trim();
                        try {
                            com.fasterxml.jackson.databind.JsonNode node = om.readTree(candidate);
                            return om.writeValueAsString(node);
                        } catch (Exception ignore) {}
                    }
                }
            }
        }
        return null;
    }
    private String safeEcho(String raw) {
        try {
            return om.writeValueAsString(raw == null ? "" : raw);
        } catch (Exception e) {
            return "\"\"";
        }
    }
}