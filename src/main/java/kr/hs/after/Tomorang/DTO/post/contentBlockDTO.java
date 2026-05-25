package kr.hs.after.Tomorang.DTO.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "본문 콘텐츠 블록")
public class contentBlockDTO {

    @Schema(description = "블록 타입 (text / image)", example = "text")
    private String type;

    @Schema(description = "텍스트 내용 또는 이미지 URL", example = "서울의 야경은 정말 아름답습니다.")
    private String value;

    @Schema(description = "블록 순서 (0부터 시작)", example = "0")
    private int sequence;
}
