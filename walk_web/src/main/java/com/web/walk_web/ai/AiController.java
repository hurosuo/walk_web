package com.web.walk_web.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.walk_web.domain.dto.InfoDto;
import com.web.walk_web.domain.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/walk/ai")
@RequiredArgsConstructor
public class AiController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * application.yml 예시
     *
     * aws:
     *   region: us-east-1
     *   lambda:
     *     shim-name: action_group_springboot-ix2bg
     */
    @Value("us-east-1")
    private String awsRegion;

    @Value("action_group_springboot-ix2bg")
    private String shimLambdaName;

    private LambdaClient lambdaClient;

    // 첫 호출 시 LambdaClient 초기화 (빈 생성 시 region 미주입 문제 회피)
    private LambdaClient lambda() {
        if (lambdaClient == null) {
            lambdaClient = LambdaClient.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
        return lambdaClient;
    }

    /**
     * InfoDto → (샘 람다 호출) → ResponseDto
     * 요청: application/json (InfoDto)
     * 응답: application/json (ResponseDto)
     */
    @PostMapping(
            path = "/request",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> request(@RequestBody InfoDto infoDto) {
        try {
            // 1) InfoDto를 JSON 문자열로 직렬화하여 람다에 그대로 전달
            String payloadJson = objectMapper.writeValueAsString(infoDto);

            InvokeRequest invokeReq = InvokeRequest.builder()
                    .functionName(shimLambdaName)
                    .payload(SdkBytes.fromString(payloadJson, StandardCharsets.UTF_8))
                    .build();

            // 2) 샘 람다 호출
            InvokeResponse invokeRes = lambda().invoke(invokeReq);

            // 3) 람다의 RAW 페이로드(JSON 텍스트) 추출
            String raw = invokeRes.payload() == null
                    ? ""
                    : invokeRes.payload().asString(StandardCharsets.UTF_8);

            // 4) 람다 에러(Handled) 확인: FunctionError가 있으면 500으로 에러 리턴
            if (invokeRes.functionError() != null && !invokeRes.functionError().isBlank()) {
                return ResponseEntity.status(500).body(Map.of(
                        "ok", false,
                        "error", "Lambda functionError: " + invokeRes.functionError(),
                        "raw", raw
                ));
            }

            if (raw == null || raw.isBlank()) {
                return ResponseEntity.status(502).body(Map.of(
                        "ok", false,
                        "error", "Empty payload from Lambda"
                ));
            }

            // 5) 람다 응답(JSON)을 ResponseDto로 역직렬화
            ResponseDto response = objectMapper.readValue(raw, ResponseDto.class);

            // 6) 그대로 반환 (프론트/클라이언트에서는 ResponseDto JSON을 받게 됨)
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 어떤 예외가 나도 JSON으로 에러 반환 (서버는 멈추지 않음)
            return ResponseEntity.status(500).body(Map.of(
                    "ok", false,
                    "error", e.getClass().getSimpleName() + ": " + e.getMessage()
            ));
        }
    }
}
