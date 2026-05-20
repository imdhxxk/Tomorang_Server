package kr.hs.after.Tomorang.DTO.post;

import lombok.Data;

import java.util.List;

@Data
public class scheduleDTO {
    private Long scheduleId;
    private String date;
    private List<timeSlotDTO> timeSlots;
}
