package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.model.chatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface chatMessageDAO {

    /**
     * 메시지 저장
     */
    void insert(chatMessage chatMessage);

    /**
     * 특정 채팅방의 모든 메시지 조회 (시간순 정렬)
     */
    List<chatMessage> findByRoomIdOrderByTimestampAsc(@Param("roomId") String roomId);

    List<chatMessage> findBySenderAndRecipientOrderByTimestampAsc(
            @Param("sender") String sender,
            @Param("recipient") String recipient);

    // 채팅방의 마지막 메시지
    chatMessage findLastMessageByRoomId(@Param("roomId") String roomId);

    // 안 읽은 메시지 수 (상대방이 보낸 것 중 읽지 않은 것)
    int countUnread(@Param("roomId") String roomId, @Param("userId") String userId);

    // 채팅방 입장 시 읽음 처리
    void markAsRead(@Param("roomId") String roomId, @Param("userId") String userId);
}

