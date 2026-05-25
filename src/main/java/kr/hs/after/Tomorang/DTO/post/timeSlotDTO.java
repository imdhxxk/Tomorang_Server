package kr.hs.after.Tomorang.DTO.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "시간 슬롯")
public class timeSlotDTO {

    @Schema(description = "슬롯 고유 ID", example = "slot_20260601_1000")
    private String id;

    @Schema(description = "시작 시간", example = "10:00")
    private String time;

    @Schema(description = "슬롯 상태 (OPEN / CLOSED)", example = "OPEN")
    private String status;

    @Schema(description = "최대 수용 인원", example = "10")
    private int maxCapacity;

    @Schema(description = "현재 예약된 인원", example = "3")
    private int bookedCount;
}
