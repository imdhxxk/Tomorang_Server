package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.model.chatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface chatRoomDAO {

    /**
     * 채팅방 저장
     */
    void insert(chatRoom chatRoom);

    /**
     * 채팅방 ID로 조회
     */
    Optional<chatRoom> findByRoomId(@Param("roomId") String roomId);

    /**
     * 두 사용자 간의 채팅방 찾기 (순서 무관)
     */
    Optional<chatRoom> findByUsers(@Param("user1") String user1,
                                   @Param("user2") String user2);
}

