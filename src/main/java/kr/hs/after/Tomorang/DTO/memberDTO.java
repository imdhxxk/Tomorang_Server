package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "회원 정보")
public class memberDTO {

    @Schema(description = "아이디", example = "user123")
    private String id;

    @Schema(description = "비밀번호 (회원가입·수정 시만 입력)", example = "password123")
    private String pw;

    @Schema(description = "역할 (GUIDE / TRAVELER)", example = "GUIDE")
    private String role;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "관심사", example = "등산, 맛집")
    private String interest;

    @Schema(description = "프로필 이미지 URL (S3)")
    private String image;

    @Schema(description = "닉네임", example = "서울가이드김")
    private String nickName;

    @Schema(description = "한 줄 소개", example = "서울 골목 여행 전문 가이드입니다.")
    private String oneWord;

    @Schema(description = "사용 언어 목록 (KOREAN / ENGLISH / JAPANESE)",
            example = "[\"KOREAN\", \"ENGLISH\"]")
    private List<LanguageType> languages;

    @Schema(description = "언어별 레벨 (1: 기초 / 2: 중급 / 3: 고급) — languages와 순서 일치",
            example = "[3, 2]")
    private List<Integer> levels;

    @Schema(description = "국적 (프론트는 \"한국\"/\"일본\" 문자열 전송)", example = "한국", nullable = true)
    private String nationality;

    @Schema(description = "기본 언어 코드", example = "ko", allowableValues = {"ko", "ja"}, nullable = true)
    private String defaultLanguage;

    @Schema(description = "평균 답변시간 (가이드 전시용)", example = "평균 12분 내로 응답", nullable = true)
    private String avgAnswerTime;

    // 프론트 호환: answerTime / averageAnswerTime / average_answer_time 로도 같은 값 노출
    @com.fasterxml.jackson.annotation.JsonProperty("answerTime")
    public String getAnswerTime() { return avgAnswerTime; }
    @com.fasterxml.jackson.annotation.JsonProperty("averageAnswerTime")
    public String getAverageAnswerTime() { return avgAnswerTime; }
    @com.fasterxml.jackson.annotation.JsonProperty("average_answer_time")
    public String getAverageAnswerTimeSnake() { return avgAnswerTime; }
}
