package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.DTO.notificationDTO;
import kr.hs.after.Tomorang.Service.notificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "알림", description = "예약 확정/거절·리뷰 작성 시 자동 생성되는 알림 조회 및 읽음 처리")
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class notificationController {

    private final notificationService notificationService;

    /* ───────────── 내 알림 목록 ───────────── */
    @Operation(
        summary = "내 알림 목록 조회",
        description = "receiver_id = userId 기준으로 본인 알림만 최신순(createdAt DESC)으로 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = notificationDTO.class)))
    @GetMapping
    public ResponseEntity<List<notificationDTO>> getMyNotifications(
            @Parameter(description = "내 사용자 ID", required = true, example = "traveler1")
            @RequestParam String userId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    /* ───────────── 단건 읽음 처리 ───────────── */
    @Operation(summary = "알림 읽음 처리", description = "알림 1건을 읽음 상태로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "처리 성공 (`{success:true}`)")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @Parameter(description = "알림 ID", required = true, example = "1")
            @PathVariable Long notificationId) {
        boolean ok = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    /* ───────────── 전체 읽음 처리 ───────────── */
    @Operation(summary = "내 알림 전체 읽음 처리")
    @ApiResponse(responseCode = "200", description = "처리 성공 (`{success:true}`)")
    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @Parameter(description = "내 사용자 ID", required = true, example = "traveler1")
            @RequestParam String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /* ───────────── 안 읽은 개수 ───────────── */
    @Operation(summary = "안 읽은 알림 개수 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공 (`{count:N}`)")
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> unreadCount(
            @Parameter(description = "내 사용자 ID", required = true, example = "traveler1")
            @RequestParam String userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(userId)));
    }
}
