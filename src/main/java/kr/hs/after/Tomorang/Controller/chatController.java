package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.DTO.chatMessageDTO;
import kr.hs.after.Tomorang.DTO.chatRoomSummaryDTO;
import kr.hs.after.Tomorang.model.chatRoom;
import kr.hs.after.Tomorang.Service.chatRoomService;
import kr.hs.after.Tomorang.Service.chatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "채팅", description = "채팅방 생성·조회, 메시지 히스토리, 읽음 처리 (실시간: WebSocket STOMP)")
@RestController
@RequiredArgsConstructor
@Slf4j
public class chatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final chatService chatService;
    private final chatRoomService chatRoomService;

    /* ───────────── WebSocket ───────────── */
    @Operation(
        summary = "[WebSocket] 메시지 전송",
        description = """
                **STOMP 연결 후 사용하는 WebSocket 엔드포인트입니다. REST 호출 불가.**

                | 항목 | 값 |
                |------|-----|
                | 연결 URL | `ws://host/ws` |
                | 구독 (수신) | `/user/queue/messages` |
                | 룸 구독 | `/topic/room/{roomId}` |
                | 전송 | `/app/chat` |

                전송 시 roomId, sender, recipient, content, type(CHAT) 을 포함해 주세요.
                """
    )
    @MessageMapping("/chat")
    public void processMessage(@Payload chatMessageDTO chatMessage) {
        chatMessageDTO savedMessage = chatService.saveMessage(chatMessage);

        messagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", savedMessage);
        messagingTemplate.convertAndSendToUser(chatMessage.getSender(),    "/queue/messages", savedMessage);

        if (chatMessage.getRoomId() != null) {
            messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), savedMessage);
        }
    }

    /* ───────────── 채팅방 생성 / 조회 ───────────── */
    @Operation(
        summary = "채팅방 생성 또는 조회",
        description = "두 사용자 간의 채팅방이 이미 존재하면 기존 채팅방을, 없으면 새로 생성하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "채팅방 정보 반환 (roomId, user1, user2)")
    @PostMapping("/api/chat/room")
    public ResponseEntity<Map<String, String>> createOrGetChatRoom(
            @Parameter(description = "사용자1 ID", required = true, example = "user123") @RequestParam String user1,
            @Parameter(description = "사용자2 ID", required = true, example = "guide456") @RequestParam String user2) {

        chatRoom room = chatRoomService.getOrCreateChatRoom(user1, user2);
        Map<String, String> response = new HashMap<>();
        response.put("roomId",  room.getRoomId());
        response.put("user1",   room.getUser1());
        response.put("user2",   room.getUser2());
        return ResponseEntity.ok(response);
    }

    /* ───────────── 채팅방 목록 ───────────── */
    @Operation(
        summary = "내 채팅방 목록 조회",
        description = "내가 참여 중인 채팅방 목록을 반환합니다. 마지막 메시지와 안 읽은 메시지 수가 포함됩니다."
    )
    @ApiResponse(responseCode = "200", description = "채팅방 목록",
            content = @Content(schema = @Schema(implementation = chatRoomSummaryDTO.class)))
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<chatRoomSummaryDTO>> getChatRooms(
            @Parameter(description = "내 사용자 ID", required = true, example = "user123")
            @RequestParam String userId) {
        return ResponseEntity.ok(chatService.getRoomSummaries(userId));
    }

    /* ───────────── 채팅 히스토리 (roomId) ───────────── */
    @Operation(
        summary = "채팅 히스토리 조회 (채팅방 ID)",
        description = "채팅방 ID로 해당 방의 전체 메시지를 시간 오름차순으로 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "메시지 목록",
            content = @Content(schema = @Schema(implementation = chatMessageDTO.class)))
    @GetMapping("/api/chat/history/{roomId}")
    public ResponseEntity<List<chatMessageDTO>> getChatHistory(
            @Parameter(description = "채팅방 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getChatHistory(roomId));
    }

    /* ───────────── 채팅 히스토리 (두 사용자) ───────────── */
    @Operation(
        summary = "채팅 히스토리 조회 (두 사용자)",
        description = "두 사용자 ID로 서로 주고받은 전체 메시지를 시간 오름차순으로 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "메시지 목록",
            content = @Content(schema = @Schema(implementation = chatMessageDTO.class)))
    @GetMapping("/api/chat/history")
    public ResponseEntity<List<chatMessageDTO>> getChatHistoryBetweenUsers(
            @Parameter(description = "사용자1 ID", required = true, example = "user123") @RequestParam String user1,
            @Parameter(description = "사용자2 ID", required = true, example = "guide456") @RequestParam String user2) {
        return ResponseEntity.ok(chatService.getChatHistoryBetweenUsers(user1, user2));
    }

    /* ───────────── 읽음 처리 ───────────── */
    @Operation(
        summary = "메시지 읽음 처리",
        description = "채팅방에 입장할 때 호출합니다. 해당 방에서 내가 수신자인 미읽음 메시지를 모두 읽음 처리합니다."
    )
    @ApiResponse(responseCode = "200", description = "읽음 처리 완료")
    @PatchMapping("/api/chat/room/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable String roomId,
            @Parameter(description = "내 사용자 ID", required = true, example = "user123") @RequestParam String userId) {
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    /* ───────────── 헬스 체크 ───────────── */
    @Operation(summary = "서버 상태 확인", description = "서버가 정상 동작 중인지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "서버 정상")
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "WebSocket Chat Server is running (MyBatis)");
        return ResponseEntity.ok(response);
    }
}
