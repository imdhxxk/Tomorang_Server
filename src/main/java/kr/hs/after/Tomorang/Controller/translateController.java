package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "번역", description = "채팅 메시지 번역 (DeepL API)")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class translateController {

    @Value("${deepl.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Free 플랜: api-free.deepl.com / Pro 플랜: api.deepl.com
    private static final String DEEPL_URL = "https://api-free.deepl.com/v2/translate";

    @Operation(
        summary = "메시지 번역",
        description = """
                채팅 메시지를 원하는 언어로 번역합니다. (DeepL API 사용)

                **주요 언어 코드:**
                | 코드 | 언어     |
                |------|----------|
                | KO   | 한국어   |
                | EN   | 영어     |
                | JA   | 일본어   |
                | ZH   | 중국어   |
                | FR   | 프랑스어 |
                | DE   | 독일어   |
                | ES   | 스페인어 |

                소스 언어를 지정하지 않으면 DeepL이 자동 감지합니다.
                """
    )
    @ApiResponse(responseCode = "200", description = "번역 성공")
    @ApiResponse(responseCode = "400", description = "번역 실패")
    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> translate(
            @Parameter(description = "번역할 원본 텍스트", required = true, example = "Hello! How are you?")
            @RequestParam String text,
            @Parameter(description = "대상 언어 코드 (기본: KO)", example = "KO")
            @RequestParam(defaultValue = "KO") String targetLang,
            @Parameter(description = "소스 언어 코드 (생략 시 자동감지)", example = "EN")
            @RequestParam(required = false) String sourceLang) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "DeepL-Auth-Key " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("text", List.of(text));
            body.put("target_lang", targetLang.toUpperCase());
            if (sourceLang != null && !sourceLang.isBlank()) {
                body.put("source_lang", sourceLang.toUpperCase());
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(DEEPL_URL, request, Map.class);

            @SuppressWarnings("unchecked")
            List<Map<String, String>> translations =
                    (List<Map<String, String>>) response.getBody().get("translations");

            String translatedText = translations.get(0).get("text");
            String detectedLang   = translations.get(0).get("detected_source_language");

            return ResponseEntity.ok(Map.of(
                    "originalText",   text,
                    "translatedText", translatedText,
                    "detectedLang",   detectedLang != null ? detectedLang : (sourceLang != null ? sourceLang : "auto"),
                    "targetLang",     targetLang.toUpperCase()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "번역 실패: " + e.getMessage()
            ));
        }
    }
}
