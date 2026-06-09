package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "숨긴 사용자 (프로필 포함)")
public class hiddenUserDTO {

    @Schema(description = "숨김 당한 사용자 ID", example = "guide123")
    private String hiddenUserId;

    @Schema(description = "숨김 당한 사용자 역할", example = "GUIDE")
    private String role;

    @Schema(description = "닉네임", example = "Yuri")
    private String nickName;

    @Schema(description = "프로필 이미지 URL", example = "https://.../yuri.jpg")
    private String image;

    @Schema(description = "한 줄 소개", example = "안녕하세요 유리입니다.")
    private String oneWord;

    @Schema(description = "숨긴 시각")
    private LocalDateTime hiddenAt;
}
