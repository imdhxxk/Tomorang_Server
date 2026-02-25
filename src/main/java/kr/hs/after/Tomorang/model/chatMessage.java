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
    private String roomId;      // 채팅방 ID
    private String sender;      // 발신자
    private String recipient;   // 수신자
    private String content;     // 메시지 내용
    private LocalDateTime timestamp;
    private MessageType type;   // 메시지 타입

    public enum MessageType {
        CHAT,    // 일반 채팅 메시지
        JOIN,    // 입장
        LEAVE    // 퇴장
    }
}

