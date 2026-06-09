package kr.hs.after.Tomorang.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "채팅방 목록 요약 정보")
public class chatRoomSummaryDTO {

    @Schema(description = "채팅방 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String roomId;

    @Schema(description = "상대방 사용자 ID", example = "guide123")
    private String otherUser;

    @Schema(description = "마지막 메시지 내용", example = "내일 오전 10시 어떠세요?")
    private String lastMessage;

    @Schema(description = "마지막 메시지 시간")
    private LocalDateTime lastMessageTime;

    @Schema(description = "안 읽은 메시지 수", example = "3")
    private int unreadCount;

    // ── 연결된 예약/게시글 정보 (roomId(UUID)와 절대 혼동 금지) ──

    @Schema(description = "연결된 예약 ID (숫자)", example = "10")
    private Long reservationId;

    @Schema(description = "연결된 게시글 ID (숫자). roomId(UUID)와 다름!", example = "3")
    private Long postId;

    @Schema(description = "게시글 제목", example = "신림역 근처 한바퀴")
    private String postTitle;

    @Schema(description = "게시글 대표 이미지 URL", example = "https://.../thumb.jpg")
    private String thumbnailUrl;
}
