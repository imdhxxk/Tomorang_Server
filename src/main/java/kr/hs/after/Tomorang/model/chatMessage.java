package kr.hs.after.Tomorang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class chatMessage {

    private Long id;
    private String roomId;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;
    private Boolean isRead;

    public enum MessageType {
        CHAT,    // 일반 채팅 메시지
        JOIN,    // 입장
        LEAVE    // 퇴장
    }
}

