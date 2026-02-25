package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.model.chatMessage;
import kr.hs.after.Tomorang.DAO.chatMessageDAO;
import kr.hs.after.Tomorang.DTO.chatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class chatService {

    private final chatMessageDAO chatMessageMapper;

    /**
     * 메시지 저장
     */
    @Transactional
    public chatMessageDTO saveMessage(chatMessageDTO messageDTO) {
        chatMessage message = messageDTO.toEntity();

        // timestamp가 null이면 현재 시간으로 설정
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }

        chatMessageMapper.insert(message);
        log.info("Message saved: {}", message);

        return chatMessageDTO.fromEntity(message);
    }

    /**
     * 특정 채팅방의 모든 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<chatMessageDTO> getChatHistory(String roomId) {
        List<chatMessage> messages = chatMessageMapper.findByRoomIdOrderByTimestampAsc(roomId);
        return messages.stream()
                .map(chatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 두 사용자 간의 모든 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<chatMessageDTO> getChatHistoryBetweenUsers(String user1, String user2) {
        List<chatMessage> messages = chatMessageMapper
                .findBySenderAndRecipientOrderByTimestampAsc(user1, user2);
        return messages.stream()
                .map(chatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
