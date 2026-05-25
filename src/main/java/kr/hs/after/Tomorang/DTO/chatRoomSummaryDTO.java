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
}
