package kr.hs.after.Tomorang.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "알림")
public class notificationDTO {

    @Schema(description = "알림 ID", example = "1")
    private Long notificationId;

    @Schema(description = "받는 사람 ID", example = "traveler1")
    private String receiverId;

    @Schema(description = "보낸 사람 ID (선택)", example = "guide1")
    private String senderId;

    @Schema(description = "알림 종류",
            allowableValues = {"RESERVATION_CONFIRMED", "RESERVATION_REJECTED", "REVIEW_CREATED"},
            example = "RESERVATION_CONFIRMED")
    private String type;

    @Schema(description = "제목", example = "예약이 확정되었어요!")
    private String title;

    @Schema(description = "내용", example = "[구마모토 맛집 탐방] 2026년 02월 24일 12:00 예약을 확정하셨어요.")
    private String message;

    @Schema(description = "관련 게시물 ID (선택)", example = "10")
    private Long postId;

    @Schema(description = "관련 예약 ID (선택)", example = "3")
    private Long reservationId;

    @Schema(description = "관련 리뷰 ID (선택)")
    private Long reviewId;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "생성 시각")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
