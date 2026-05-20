package kr.hs.after.Tomorang.Controller;

import kr.hs.after.Tomorang.DTO.chatMessageDTO;
import kr.hs.after.Tomorang.DTO.chatRoomSummaryDTO;
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
     * WebSocket: 메시지 전송
     * 클라이언트 → /app/chat
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload chatMessageDTO chatMessage) {
        chatMessageDTO savedMessage = chatService.saveMessage(chatMessage);

        // 수신자에게 전달
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/messages",
                savedMessage
        );

        // 발신자에게도 전달 (내 화면에서 전송 확인)
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSender(),
                "/queue/messages",
                savedMessage
        );

        // 채팅방 구독자 전체에게 브로드캐스트 (방 기반 UI 지원)
        if (chatMessage.getRoomId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/room/" + chatMessage.getRoomId(),
                    savedMessage
            );
        }
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
     * REST API: 내 채팅방 목록 (마지막 메시지 + 안읽은 수 포함)
     * GET /api/chat/rooms?userId=
     */
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<chatRoomSummaryDTO>> getChatRooms(@RequestParam String userId) {
        return ResponseEntity.ok(chatService.getRoomSummaries(userId));
    }

    /**
     * REST API: 채팅방 입장 — 읽음 처리
     * PATCH /api/chat/room/{roomId}/read?userId=
     */
    @PatchMapping("/api/chat/room/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String roomId,
            @RequestParam String userId) {
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
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
