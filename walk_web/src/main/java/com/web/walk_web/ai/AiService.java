package com.web.walk_web.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.walk_web.domain.dto.InfoDto;
import com.web.walk_web.domain.entity.AiRouteRecommend;
import lombok.Data; // 파싱용 DTO
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.bedrockagentruntime.model.*;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AiService {
    private final BedrockAgentRuntimeAsyncClient client;
    private final ObjectMapper objectMapper;

    private String agentId = "QZM7LF4IE3";
    private String agentAliasId = "ZQGHAU2QPV";
    private Logger log;

    public List<AiRouteRecommend> invokeAgentAndParse(InfoDto info) {
        String inputText = buildInputText(info);
        StringBuilder sb = new StringBuilder();

        InvokeAgentRequest req = InvokeAgentRequest.builder()
                .agentId(agentId)
                .agentAliasId(agentAliasId)
                .inputText(inputText)
                .build();

        // ✔ invokeAgent(..., handler) 는 CompletableFuture<Void> 반환 → join()만 호출
        client.invokeAgent(req, InvokeAgentResponseHandler.builder()
                .onResponse((InvokeAgentResponse r) -> { /* 헤더 등 필요시 처리 */ })
                .onEventStream((SdkPublisher<ResponseStream> publisher) -> {
                    publisher.subscribe(event -> {
                        // ✔ Visitor로 이벤트 타입별 처리
                        event.accept(InvokeAgentResponseHandler.Visitor.builder()
                                .onChunk((PayloadPart chunk) -> {
                                    sb.append(chunk.bytes().asUtf8String());
                                })
                                .onTrace((TracePart trace) -> {
                                    // 필요시 로깅
                                })
                                .onFiles((FilePart files) -> {
                                    // 파일 스트림 사용 안함
                                })
                                .onReturnControl((ReturnControlPayload rc) -> {
                                    // control 이벤트 무시
                                })
                                .onDefault((ResponseStream unknown) -> {
                                    // no-op
                                })
                                .build());
                    });
                })
                .onError(t -> { throw new RuntimeException("Agent streaming error", t); })
                .onComplete(() -> { /* done */ })
                .build()
        ).join();

        String json = sb.toString().trim();

        // 👇 바로 여기서 로그 찍기
        log.error("[Bedrock raw] >>> {}", json.substring(0, Math.min(400, json.length())));

        // 기대 형태: {"items":[ { ... }, { ... }, { ... } ]}
        try {
            var root = objectMapper.readTree(json);
            var itemsNode = root.has("items") ? root.get("items") : root;

            List<AiRouteRecommendPayload> payloads;
            if (itemsNode.isArray()) {
                payloads = objectMapper.convertValue(itemsNode, new TypeReference<>() {});
            } else {
                payloads = List.of(objectMapper.treeToValue(itemsNode, AiRouteRecommendPayload.class));
            }

            // ✔ 엔티티는 setter 없음 → Builder로 변환
            return payloads.stream()
                    .map(p -> toEntity(p, info))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Agent JSON: " + json, e);
        }
    }

    private AiRouteRecommend toEntity(AiRouteRecommendPayload p, InfoDto input) {
        // 누락값 보정
        var duration = p.duration != null ? p.duration
                : AiRouteRecommend.RouteDuration.valueOf(input.getDuration().name());
        var purpose = p.purpose != null ? p.purpose
                : AiRouteRecommend.RoutePurpose.valueOf(input.getPurpose().name());

        var startX = p.routeStartX != null ? p.routeStartX : input.getLongitude();
        var startY = p.routeStartY != null ? p.routeStartY : input.getLatitude();

        var startTime = p.routeStartTime != null ? p.routeStartTime : java.time.LocalDateTime.now();
        var endTime   = p.routeEndTime   != null ? p.routeEndTime   : startTime.plusMinutes(30);

        var addr = (p.addressJibun != null && !p.addressJibun.isBlank())
                ? p.addressJibun : input.getAddressJibun();

        return AiRouteRecommend.builder()
                .title(nvl(p.title, "산책 추천"))
                .content(nvl(p.content, "자세한 경로 설명"))
                .summary(nvl(p.summary, "요약"))
                .distanceInKm(p.distanceInKm)
                .duration(duration)
                .purpose(purpose)
                .routeStartX(startX)
                .routeStartY(startY)
                .routeStartTime(startTime)
                .routeEndTime(endTime)
                .addressJibun(addr)
                .withPet(p.withPet != null ? p.withPet : input.isWithPet())
                .build();
    }

    private static String nvl(String s, String d) { return (s == null || s.isBlank()) ? d : s; }

    private String buildInputText(InfoDto info) {
        return """
            You are a route recommender.
            Return ONLY valid JSON (no markdown, no code fences).
            Schema:
            {"items":[{"title":"string","content":"string","summary":"string","distanceInKm":1.23,
            "duration":"MIN_15|MIN_30|MIN_45|MIN_60","purpose":"CITY|NIGHT_VIEW|QUIET|SCENERY",
            "routeStartX":127.0,"routeStartY":37.0,"routeStartTime":"yyyy-MM-dd'T'HH:mm:ss",
            "routeEndTime":"yyyy-MM-dd'T'HH:mm:ss","addressJibun":"string","withPet":true}]}
            Constraints:
            - Output top 3 items for the given input.
            - All times must be Asia/Seoul in ISO-8601.
            INPUT:
            {
              "duration": "%s",
              "purpose": "%s",
              "addressJibun": "%s",
              "latitude": %f,
              "longitude": %f,
              "withPet": %b
            }
            """.formatted(info.getDuration(), info.getPurpose(),
                sanitize(info.getAddressJibun()),
                info.getLatitude(), info.getLongitude(), info.isWithPet());
    }
    private static String sanitize(String s){ return s==null?"":s.replace("\"","\\\""); }

    // ===== 파싱용 DTO (엔티티 아님) =====
    @Data
    public static class AiRouteRecommendPayload {
        private String title;
        private String content;
        private String summary;
        private Double distanceInKm;
        private AiRouteRecommend.RouteDuration duration;
        private AiRouteRecommend.RoutePurpose purpose;
        private Double routeStartX;
        private Double routeStartY;
        private java.time.LocalDateTime routeStartTime;
        private java.time.LocalDateTime routeEndTime;
        private String addressJibun;
        private Boolean withPet;
    }
}
