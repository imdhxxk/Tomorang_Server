package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hs.after.Tomorang.DTO.post.postDTO;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "마이페이지")
public class mypageDTO {

    @Schema(description = "회원 ID")
    private String id;

    @Schema(description = "닉네임")
    private String nickName;

    @Schema(description = "프로필 이미지 URL")
    private String image;

    @Schema(description = "한 줄 소개")
    private String oneWord;

    @Schema(description = "역할 (GUIDE / DISCOVERER)")
    private String role;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "관심사")
    private String interest;

    @Schema(description = "사용 언어 목록")
    private List<LanguageType> languages;

    @Schema(description = "언어별 레벨")
    private List<Integer> levels;

    @Schema(description = "찜한 게시물 목록")
    private List<postDTO> wishlists;
}
