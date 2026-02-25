package kr.hs.after.Tomorang.Controller;

import kr.hs.after.Tomorang.DTO.chatMessageDTO;
import kr.hs.after.Tomorang.model.chatRoom;
import kr.hs.after.Tomorang.Service.chatRoomService;
import lombok.RequiredArgsConstructor;
import kr.hs.after.Tomorang.Service.chatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class chatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final chatService chatService;
    private final chatRoomService chatRoomService;

    /**
     * WebSocket을 통해 메시지 수신 및 전달
     * 클라이언트가 /app/chat으로 메시지를 보내면 이 메서드가 처리
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload chatMessageDTO chatMessage) {
        log.info("Received message: {}", chatMessage);

        // 메시지 DB에 저장
        chatMessageDTO savedMessage = chatService.saveMessage(chatMessage);

        // 수신자에게 메시지 전송 (개인 메시지)
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/messages",
                savedMessage
        );

        log.info("Message sent to user: {}", chatMessage.getRecipient());
    }

    /**
     * REST API: 채팅방 생성 또는 조회
     * POST /api/chat/room
     */
    @PostMapping("/api/chat/room")
    public ResponseEntity<Map<String, String>> createOrGetChatRoom(
            @RequestParam String user1,
            @RequestParam String user2) {

        chatRoom chatRoom = chatRoomService.getOrCreateChatRoom(user1, user2);

        Map<String, String> response = new HashMap<>();
        response.put("roomId", chatRoom.getRoomId());
        response.put("user1", chatRoom.getUser1());
        response.put("user2", chatRoom.getUser2());

        return ResponseEntity.ok(response);
    }

    /**
     * REST API: 채팅 히스토리 조회 (채팅방 ID로)
     * GET /api/chat/history/{roomId}
     */
    @GetMapping("/api/chat/history/{roomId}")
    public ResponseEntity<List<chatMessageDTO>> getChatHistory(@PathVariable String roomId) {
        List<chatMessageDTO> messages = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(messages);
    }

    /**
     * REST API: 두 사용자 간의 채팅 히스토리 조회
     * GET /api/chat/history
     */
    @GetMapping("/api/chat/history")
    public ResponseEntity<List<chatMessageDTO>> getChatHistoryBetweenUsers(
            @RequestParam String user1,
            @RequestParam String user2) {

        List<chatMessageDTO> messages = chatService.getChatHistoryBetweenUsers(user1, user2);
        return ResponseEntity.ok(messages);
    }

    /**
     * REST API: 헬스 체크
     * GET /api/health
     */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "WebSocket Chat Server is running (MyBatis)");
        return ResponseEntity.ok(response);
    }
}
