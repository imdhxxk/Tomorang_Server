package kr.hs.after.Tomorang.DTO.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "예약 가능 일정")
public class scheduleDTO {

    @Schema(description = "일정 ID (DB 자동 생성)", example = "1")
    private Long scheduleId;

    @Schema(description = "예약 가능 날짜", example = "2026-06-01")
    private String date;

    @Schema(description = "시간 슬롯 목록")
    private List<timeSlotDTO> timeSlots;
}
