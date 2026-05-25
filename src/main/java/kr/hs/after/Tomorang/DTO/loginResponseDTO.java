package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "로그인 응답")
public class loginResponseDTO {

    @Schema(description = "JWT 액세스 토큰")
    private String token;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String type;

    @Schema(description = "로그인한 사용자 ID", example = "user123")
    private String id;

    @Schema(description = "닉네임", example = "서울가이드김")
    private String nickName;
}
