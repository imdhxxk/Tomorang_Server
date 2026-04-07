package kr.hs.after.Tomorang.DTO.post;

import lombok.Data;

import java.util.List;

@Data
public class scheduleDTO {
    private String date;
    private List<timeSlotDTO> timeSlots;
}
