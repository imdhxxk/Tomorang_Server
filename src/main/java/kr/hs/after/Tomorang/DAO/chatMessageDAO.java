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

    /**
     * 두 사용자 간의 모든 메시지 조회
     */
    List<chatMessage> findBySenderAndRecipientOrderByTimestampAsc(
            @Param("sender") String sender,
            @Param("recipient") String recipient);
}

