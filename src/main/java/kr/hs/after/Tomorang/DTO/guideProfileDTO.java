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

    @Schema(description = "평균 답변시간 (전시용)", example = "평균 12분 내로 응답", nullable = true)
    private String avgAnswerTime;

    // 프론트 호환: answerTime / averageAnswerTime / average_answer_time 로도 같은 값 노출
    @com.fasterxml.jackson.annotation.JsonProperty("answerTime")
    public String getAnswerTime() { return avgAnswerTime; }
    @com.fasterxml.jackson.annotation.JsonProperty("averageAnswerTime")
    public String getAverageAnswerTime() { return avgAnswerTime; }
    @com.fasterxml.jackson.annotation.JsonProperty("average_answer_time")
    public String getAverageAnswerTimeSnake() { return avgAnswerTime; }
}
