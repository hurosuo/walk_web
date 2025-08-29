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
    // 콘솔에서 만든 “샘 람다” 이름
    private static final String SHIM_LAMBDA_NAME = "action_group_springboot-ix2bg";

    private final ObjectMapper om = new ObjectMapper();
    private final LambdaClient lambda = LambdaClient.builder()
            .region(REGION)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody InfoDto dto) {
        try {
            byte[] payload = om.writeValueAsBytes(dto);
            var req = InvokeRequest.builder()
                    .functionName("action_group_springboot-ix2bg") // 또는 SHIM_LAMBDA_NAME 사용 가능
                    .payload(SdkBytes.fromByteArray(payload))
                    .build();

            var resp = lambda.invoke(req);
            // Lambda 원문 응답 텍스트 (FunctionError가 있어도 payload는 내려옴)
            String raw = resp.payload() == null ? "" : resp.payload().asString(StandardCharsets.UTF_8);
            String functionError = resp.functionError(); // e.g., "Unhandled" (null 일 수도 있음)

            // 1) 우선 샘 람다 규격 시도: {"ok":true/false,"message":"...텍스트(여기에 JSON 포함 가능)..."}
            String candidateText = raw;
            try {
                JsonNode root = om.readTree(raw);
                // ok / message 규격을 따른다면 message를 후보 텍스트로 사용 (asText가 이스케이프를 풀어줌)
                if (root.has("message")) {
                    candidateText = root.path("message").asText();
                }
            } catch (Exception ignore) {
                // raw가 순수 JSON이 아닐 수 있으니 무시하고 원문에서 추출 시도
            }

            // 2) 후보 텍스트(= message 또는 raw)에서 가장 바깥 JSON 객체 하나를 뽑아낸다
            String jsonStr = extractOutermostJson(candidateText);
            if (jsonStr == null) {
                // message 안에 없었다면, 원문 raw 전체에서도 한 번 더 시도
                jsonStr = extractOutermostJson(raw);
            }

            if (jsonStr != null) {
                // 3) 뽑아낸 JSON을 ResponseDto로 변환 (알 수 없는 필드는 무시하려면 ResponseDto에 @JsonIgnoreProperties(ignoreUnknown = true) 권장)
                ResponseDto responseDto = om.readValue(jsonStr, ResponseDto.class);

                // 함수 자체는 에러였어도 우리가 유효 JSON을 얻었으면 200으로 응답
                return ResponseEntity.ok(responseDto);
            }

            // 4) 여기까지 왔다면 JSON을 못 구한 케이스 — 원문과 FunctionError를 그대로 노출
            String err = (functionError != null) ? functionError : "UnknownError";
            return ResponseEntity.internalServerError()
                    .body("{\"ok\":false,\"error\":\"" + err + "\",\"payload\":" + safeEcho(raw) + "}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"ok\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // 텍스트에서 가장 바깥 JSON 객체 하나를 안전하게 추출 (따옴표/이스케이프/중첩 대응)
    private String extractOutermostJson(String text) {
        if (text == null || text.isEmpty()) return null;

        boolean inString = false;   // " 문자열 내부 여부
        boolean escaped  = false;   // 직전 문자가 \ 였는지
        int depth = 0;
        int start = -1;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (inString) {
                if (escaped) {
                    // 직전이 백슬래시였다면 현재 문자는 이스케이프된 문자이므로 그대로 소비
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }

            // 문자열 밖인 경우
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
                        // 유효 JSON인지 검증 후 정규화하여 반환
                        try {
                            com.fasterxml.jackson.databind.JsonNode node = om.readTree(candidate);
                            return om.writeValueAsString(node);
                        } catch (Exception ignore) {
                            // 유효하지 않으면 계속 탐색
                        }
                    }
                }
            }
        }
        return null;
    }

    // 에러 응답용으로 원문을 안전하게 JSON 문자열로 이스케이프
    private String safeEcho(String raw) {
        try {
            return om.writeValueAsString(raw == null ? "" : raw);
        } catch (Exception e) {
            return "\"\"";
        }
    }
}
