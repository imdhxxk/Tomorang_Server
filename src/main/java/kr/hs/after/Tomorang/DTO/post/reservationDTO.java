package kr.hs.after.Tomorang.DTO.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "예약")
public class reservationDTO {

    @Schema(description = "예약 ID (조회 시 반환)")
    private Long id;

    @Schema(description = "게시물 ID", example = "1")
    private Long postId;

    @Schema(description = "게시물 제목 (조회 시 반환)")
    private String postTitle;

    @Schema(description = "예약 신청자(발견자) ID (조회 시 반환)", example = "discoverer1")
    private String requesterId;

    @Schema(description = "예약 신청자 ID (requesterId와 동일, 프론트 호환용)", example = "discoverer1")
    private String memberId;

    @Schema(description = "게시글 작성자(가이드) ID", example = "guide1")
    private String guideId;

    @Schema(description = "연결된 채팅방 ID (UUID). 예약 수락 시 생성/연결되어 채워짐", example = "550e8400-e29b-41d4-a716-446655440000")
    private String chatRoomId;

    @Schema(description = "타임슬롯 ID", example = "slot_20260601_1000")
    private String slotId;

    @Schema(description = "예약 날짜 (조회 시 반환)", example = "2026-06-01")
    private String slotDate;

    @Schema(description = "예약 시간 (조회 시 반환)", example = "10:00")
    private String slotTime;

    @Schema(description = "성인 인원 (0 이상)", example = "2")
    private int adultCount;

    @Schema(description = "어린이 인원 (0 이상)", example = "1")
    private int childCount;

    @Schema(description = "요청사항 / 메모 (선택)", example = "아이 동반입니다. 천천히 부탁드려요.")
    private String request;

    @Schema(
        description = """
                예약 상태
                - PENDING: 발견자가 신청, 가이드 확인 전 (신청 직후 기본값)
                - CONFIRMED: 가이드가 수락함 (이후 채팅 가능)
                - REJECTED: 가이드가 거절함
                - COMPLETED: 투어 완료
                - CANCELLED: 발견자/시스템 취소
                """,
        allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "COMPLETED", "CANCELLED"},
        example = "PENDING")
    private String status;

    @Schema(description = "예약 일시 (조회 시 반환)")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
