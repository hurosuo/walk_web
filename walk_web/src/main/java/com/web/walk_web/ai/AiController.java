package com.web.walk_web.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.walk_web.domain.dto.InfoDto;
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
                    .functionName("action_group_springboot-ix2bg")
                    .payload(SdkBytes.fromByteArray(payload))
                    .build();

            var resp = lambda.invoke(req);
            String body = resp.payload().asString(StandardCharsets.UTF_8);

            // 샘 람다에서 {"ok":true,"message":"..."} 로 내려줌
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"ok\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
