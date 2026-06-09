package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DAO.chatRoomDAO;
import kr.hs.after.Tomorang.model.chatMessage;
import kr.hs.after.Tomorang.model.chatRoom;
import kr.hs.after.Tomorang.DAO.chatMessageDAO;
import kr.hs.after.Tomorang.DTO.chatMessageDTO;
import kr.hs.after.Tomorang.DTO.chatRoomSummaryDTO;
import kr.hs.after.Tomorang.DTO.roomContextDTO;
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
    private final chatRoomDAO chatRoomMapper;

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

    /**
     * 내가 참여한 채팅방 목록 (마지막 메시지 + 안읽은 수 포함)
     */
    @Transactional(readOnly = true)
    public List<chatRoomSummaryDTO> getRoomSummaries(String userId) {
        List<chatRoom> rooms = chatRoomMapper.findRoomsByUser(userId);
        return rooms.stream().map(room -> {
            String otherUser = room.getUser1().equals(userId) ? room.getUser2() : room.getUser1();
            chatMessage last = chatMessageMapper.findLastMessageByRoomId(room.getRoomId());
            int unread = chatMessageMapper.countUnread(room.getRoomId(), userId);

            // 예약→게시글 컨텍스트 (postId는 숫자, roomId(UUID)와 분리)
            roomContextDTO ctx = chatRoomMapper.findRoomContext(room.getUser1(), room.getUser2());

            return chatRoomSummaryDTO.builder()
                    .roomId(room.getRoomId())
                    .otherUser(otherUser)
                    .lastMessage(last != null ? last.getContent() : null)
                    .lastMessageTime(last != null ? last.getTimestamp() : room.getCreatedAt())
                    .unreadCount(unread)
                    .reservationId(ctx != null ? ctx.getReservationId() : null)
                    .postId(ctx != null ? ctx.getPostId() : null)
                    .postTitle(ctx != null ? ctx.getPostTitle() : null)
                    .thumbnailUrl(ctx != null ? ctx.getThumbnailUrl() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 채팅방 입장 시 읽음 처리
     */
    @Transactional
    public void markAsRead(String roomId, String userId) {
        chatMessageMapper.markAsRead(roomId, userId);
    }
}
