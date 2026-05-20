package kr.hs.after.Tomorang.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class loginResponseDTO {
    private String token;
    private String type;
    private String id;
    private String nickName;
}