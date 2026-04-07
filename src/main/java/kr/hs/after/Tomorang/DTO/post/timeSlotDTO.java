package kr.hs.after.Tomorang.DTO.post;

import lombok.Data;

@Data
public class timeSlotDTO {
    private String id; // slot_2026...
    private String time;
    private String status; // OPEN, CLOSED
}
