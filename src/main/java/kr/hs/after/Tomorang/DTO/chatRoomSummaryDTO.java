package kr.hs.after.Tomorang.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class chatRoomSummaryDTO {
    private String roomId;
    private String otherUser;         // 상대방 ID
    private String lastMessage;       // 마지막 메시지 내용
    private LocalDateTime lastMessageTime;
    private int unreadCount;          // 안 읽은 메시지 수
}
