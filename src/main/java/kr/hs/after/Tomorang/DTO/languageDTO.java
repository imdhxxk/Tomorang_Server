package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "언어 정보")
public class languageDTO {

    @Schema(description = "언어 (KOREAN / ENGLISH / JAPANESE)", example = "KOREAN")
    private LanguageType language;

    @Schema(description = "능숙도 레벨 (1: 기초 / 2: 중급 / 3: 고급)", example = "2")
    private int level;
}
