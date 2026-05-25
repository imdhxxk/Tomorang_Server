package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.DTO.post.reservationDTO;
import kr.hs.after.Tomorang.Service.memberService;
import kr.hs.after.Tomorang.Service.post.reservationService;
import kr.hs.after.Tomorang.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "예약", description = "투어 예약 (발견자 전용) · 내 예약 목록 조회")
@RestController
@RequestMapping("/api/reservation")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class reservationController {

    private final reservationService reservationService;
    private final memberService      memberService;
    private final JwtUtil            jwtUtil;

    /* ───────────── 예약 ───────────── */
    @Operation(
        summary = "투어 예약",
        description = """
                발견자(DISCOVERER)가 원하는 시간대에 예약합니다.
                - 음수 인원 입력 시 400 오류
                - 마감된 슬롯 예약 시 400 오류
                - 안내자(GUIDE) 계정으로 예약 시 403 오류
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "예약 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 인원 / 마감된 시간대 / 잔여 인원 부족")
    @ApiResponse(responseCode = "403", description = "DISCOVERER 역할만 예약 가능")
    @PostMapping
    public ResponseEntity<Map<String, String>> book(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @RequestBody reservationDTO dto) {
        try {
            String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));

            // 발견자 역할 확인
            String role = memberService.getRole(userId);
            if (!"DISCOVERER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "발견자(DISCOVERER)만 예약할 수 있습니다."));
            }

            reservationService.book(userId, dto);
            return ResponseEntity.ok(Map.of("message", "예약이 완료되었습니다."));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* ───────────── 내 예약 목록 ───────────── */
    @Operation(
        summary = "내 예약 목록 조회",
        description = "로그인한 발견자의 전체 예약 내역을 조회합니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my")
    public ResponseEntity<List<reservationDTO>> getMyReservations(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(reservationService.getMyReservations(userId));
    }
}
