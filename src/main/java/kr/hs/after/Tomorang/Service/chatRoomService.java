package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DAO.chatRoomDAO;
import kr.hs.after.Tomorang.model.chatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class chatRoomService {

    private final chatRoomDAO chatRoomMapper;

    /**
     * 두 사용자 간의 채팅방 조회 또는 생성
     */
    @Transactional
    public chatRoom getOrCreateChatRoom(String user1, String user2) {
        // 기존 채팅방 찾기
        Optional<chatRoom> existingRoom = chatRoomMapper.findByUsers(user1, user2);

        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        // 새 채팅방 생성
        chatRoom newRoom = chatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .user1(user1)
                .user2(user2)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomMapper.insert(newRoom);
        return newRoom;
    }

    /**
     * 채팅방 ID로 채팅방 조회
     */
    public Optional<chatRoom> getChatRoomByRoomId(String roomId) {
        return chatRoomMapper.findByRoomId(roomId);
    }

    /**
     * 사용자가 채팅방에 속해있는지 확인
     */
    public boolean isUserInRoom(String roomId, String username) {
        Optional<chatRoom> room = chatRoomMapper.findByRoomId(roomId);
        return room.map(chatRoom ->
                chatRoom.getUser1().equals(username) || chatRoom.getUser2().equals(username)
        ).orElse(false);
    }
}
