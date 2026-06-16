package kr.hs.after.Tomorang.DTO.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "리뷰")
public class reviewDTO {

    @Schema(description = "리뷰 ID (조회 시 반환)")
    private Long id;

    @Schema(description = "게시물 ID", example = "1")
    private Long postId;

    @Schema(description = "작성자 ID (조회 시 반환)")
    private String memberId;

    @Schema(description = "작성자 닉네임 (조회 시 반환)")
    private String memberNickName;

    @Schema(description = "작성자 프로필 이미지 (조회 시 반환)")
    private String memberImage;

    @Schema(description = "별점 (1~5)", example = "5")
    private int rating;

    @Schema(description = "리뷰 내용", example = "정말 좋은 투어였어요!")
    private String content;

    @Schema(description = "리뷰 이미지 URL 목록 (조회 시 반환)")
    private List<String> images;

    @Schema(description = "좋아요 수 (조회 시 반환)")
    private int likeCount;

    @Schema(description = "내가 좋아요 눌렀는지 (조회 시 반환)")
    private boolean liked;

    @Schema(description = "작성 일시 (ISO-8601, 실제 DB 생성시간)", example = "2026-06-17T08:33:00")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
