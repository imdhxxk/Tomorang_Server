package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "신고")
public class reportDTO {

    @Schema(description = "신고 ID (자동 생성)", example = "1")
    private Long reportId;

    @Schema(description = "신고 대상 종류 (현재 POST만 지원)", allowableValues = {"POST"}, example = "POST")
    private String targetType;

    @Schema(description = "신고 대상 ID (게시물 숫자 ID)", example = "3")
    private Long targetId;

    @Schema(description = "신고자 ID (JWT에서 추출 — 요청 시 입력 불필요)", example = "user123")
    private String reporterId;

    @Schema(description = "신고 사유",
            allowableValues = {"SPAM", "INAPPROPRIATE", "FRAUD", "HARASSMENT", "OTHER"},
            example = "INAPPROPRIATE")
    private String reason;

    @Schema(description = "상세 내용 (선택)", example = "부적절한 게시물입니다.")
    private String content;

    @Schema(description = "처리 상태 (생성 직후 PENDING)",
            allowableValues = {"PENDING", "REVIEWED", "RESOLVED", "REJECTED"},
            example = "PENDING")
    private String status;

    @Schema(description = "신고 생성 시각")
    private LocalDateTime createdAt;
}
