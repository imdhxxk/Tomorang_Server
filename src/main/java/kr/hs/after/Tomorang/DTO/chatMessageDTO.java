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

    @io.swagger.v3.oas.annotations.media.Schema(description = "메시지 고유 ID (저장 후 부여)", example = "1")
    private Long messageId;

    private String roomId;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;
    private chatMessage.MessageType type;
    private Boolean isRead;

    public static chatMessageDTO fromEntity(chatMessage message) {
        return chatMessageDTO.builder()
                .messageId(message.getId())
                .roomId(message.getRoomId())
                .sender(message.getSender())
                .recipient(message.getRecipient())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .type(message.getType())
                .isRead(message.getIsRead())
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
                .isRead(Boolean.FALSE)
                .build();
    }
}
