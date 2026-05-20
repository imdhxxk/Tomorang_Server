package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.model.chatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    Optional<chatRoom> findByUsers(@Param("user1") String user1,
                                   @Param("user2") String user2);

    // 내가 참여한 모든 채팅방 (최신순)
    List<chatRoom> findRoomsByUser(@Param("userId") String userId);
}

