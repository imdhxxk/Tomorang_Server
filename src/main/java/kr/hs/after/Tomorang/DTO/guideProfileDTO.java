package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "안내자 프로필 (인기 순)")
public class guideProfileDTO {

    @Schema(description = "안내자 ID")
    private String id;

    @Schema(description = "닉네임")
    private String nickName;

    @Schema(description = "프로필 이미지 URL")
    private String image;

    @Schema(description = "한 줄 소개")
    private String oneWord;

    @Schema(description = "게시물 평균 평점", example = "4.7")
    private double avgRating;

    @Schema(description = "게시물 좋아요 합산", example = "128")
    private long totalLikes;

    @Schema(description = "등록 게시물 수", example = "5")
    private int postCount;

    @Schema(description = "사용 언어 목록")
    private List<LanguageType> languages;

    @Schema(description = "언어별 레벨")
    private List<Integer> levels;
}
