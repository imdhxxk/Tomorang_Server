package kr.hs.after.Tomorang.DTO;

import kr.hs.after.Tomorang.model.chatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class chatMessageDTO {

    private String roomId;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;
    private chatMessage.MessageType type;
    private boolean isRead;

    public static chatMessageDTO fromEntity(chatMessage message) {
        return chatMessageDTO.builder()
                .roomId(message.getRoomId())
                .sender(message.getSender())
                .recipient(message.getRecipient())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .type(message.getType())
                .isRead(message.isRead())
                .build();
    }

    public chatMessage toEntity() {
        return chatMessage.builder()
                .roomId(this.roomId)
                .sender(this.sender)
                .recipient(this.recipient)
                .content(this.content)
                .timestamp(this.timestamp != null ? this.timestamp : LocalDateTime.now())
                .type(this.type)
                .isRead(false)
                .build();
    }
}
