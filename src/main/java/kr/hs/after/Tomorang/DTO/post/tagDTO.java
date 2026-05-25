package kr.hs.after.Tomorang.DTO.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "다국어 태그")
public class tagDTO {

    @Schema(description = "언어 코드", example = "ko")
    private String langCode;

    @Schema(description = "태그명", example = "야경")
    private String tagName;
}
