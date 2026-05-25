package kr.hs.after.Tomorang.DTO.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "예약")
public class reservationDTO {

    @Schema(description = "예약 ID (조회 시 반환)")
    private Long id;

    @Schema(description = "게시물 ID", example = "1")
    private Long postId;

    @Schema(description = "게시물 제목 (조회 시 반환)")
    private String postTitle;

    @Schema(description = "타임슬롯 ID", example = "slot_20260601_1000")
    private String slotId;

    @Schema(description = "예약 날짜 (조회 시 반환)", example = "2026-06-01")
    private String slotDate;

    @Schema(description = "예약 시간 (조회 시 반환)", example = "10:00")
    private String slotTime;

    @Schema(description = "성인 인원 (0 이상)", example = "2")
    private int adultCount;

    @Schema(description = "어린이 인원 (0 이상)", example = "1")
    private int childCount;

    @Schema(description = "예약 상태 (CONFIRMED / CANCELLED)")
    private String status;

    @Schema(description = "예약 일시 (조회 시 반환)")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
