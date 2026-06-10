package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.NoSuchElementException;

@Tag(name = "예약", description = "투어 예약 신청(발견자) · 수락/거절(가이드) · 내 예약 목록")
@RestController
@RequestMapping("/api/reservation")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class reservationController {

    private final reservationService reservationService;
    private final memberService      memberService;
    private final JwtUtil            jwtUtil;

    private String userId(String authHeader) {
        return jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
    }

    /* ───────────── 예약 신청 ───────────── */
    @Operation(
        summary = "투어 예약 신청 (발견자)",
        description = """
                발견자(DISCOVERER)가 원하는 시간대에 예약을 신청합니다.
                - **신청 직후 상태는 항상 PENDING** 입니다. (가이드 수락 전)
                - 가이드가 수락(`/accept`)해야 CONFIRMED가 되고 채팅이 가능합니다.
                - 안내자(GUIDE) 계정으로는 예약할 수 없습니다. (403)
                - 음수 인원 / 0명 / 마감된 시간대는 400.
                - 응답으로 생성된 예약(reservationDTO, status=PENDING)을 반환합니다.
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "예약 신청 성공 (status=PENDING)",
            content = @Content(schema = @Schema(implementation = reservationDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 인원 / 마감된 시간대")
    @ApiResponse(responseCode = "403", description = "DISCOVERER 역할만 예약 가능")
    @PostMapping
    public ResponseEntity<?> book(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @RequestBody reservationDTO dto) {
        try {
            String me = userId(authHeader);
            if (!memberService.isDiscovererRole(memberService.getRole(me))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "발견자(DISCOVERER)만 예약할 수 있습니다."));
            }
            reservationDTO created = reservationService.book(me, dto);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* ───────────── 예약 수락 ───────────── */
    @Operation(
        summary = "예약 수락 (가이드)",
        description = """
                가이드가 본인 게시물에 들어온 예약을 수락합니다.
                - 해당 게시물의 작성자(가이드) 본인만 호출 가능 (403)
                - PENDING 상태일 때만 수락 가능 (이미 처리됐으면 400)
                - 수락 시 상태가 **CONFIRMED**로 변경되고, 해당 시간대 인원이 (성인+어린이)만큼 증가합니다.
                - 정원이 꽉 차면 시간대가 자동으로 CLOSED 됩니다. (정원 초과 시 400)
                - 응답으로 변경된 예약(status=CONFIRMED)을 반환합니다.
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "수락 성공 (status=CONFIRMED)",
            content = @Content(schema = @Schema(implementation = reservationDTO.class)))
    @ApiResponse(responseCode = "400", description = "이미 처리된 예약 / 잔여 인원 부족")
    @ApiResponse(responseCode = "403", description = "GUIDE 아님 / 본인 게시물 아님")
    @ApiResponse(responseCode = "404", description = "예약 없음")
    @PatchMapping("/{reservationId}/accept")
    public ResponseEntity<?> accept(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "예약 ID", required = true, example = "1")
            @PathVariable Long reservationId) {
        return handleDecision(authHeader, reservationId, true);
    }

    /* ───────────── 예약 거절 ───────────── */
    @Operation(
        summary = "예약 거절 (가이드)",
        description = """
                가이드가 본인 게시물에 들어온 예약을 거절합니다.
                - 해당 게시물의 작성자(가이드) 본인만 호출 가능 (403)
                - PENDING 상태일 때만 거절 가능 (이미 처리됐으면 400)
                - 거절 시 상태가 **REJECTED**로 변경됩니다. (인원 변동 없음)
                - 응답으로 변경된 예약(status=REJECTED)을 반환합니다.
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "거절 성공 (status=REJECTED)",
            content = @Content(schema = @Schema(implementation = reservationDTO.class)))
    @ApiResponse(responseCode = "400", description = "이미 처리된 예약")
    @ApiResponse(responseCode = "403", description = "GUIDE 아님 / 본인 게시물 아님")
    @ApiResponse(responseCode = "404", description = "예약 없음")
    @PatchMapping("/{reservationId}/reject")
    public ResponseEntity<?> reject(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "예약 ID", required = true, example = "1")
            @PathVariable Long reservationId) {
        return handleDecision(authHeader, reservationId, false);
    }

    /** 수락/거절 공통 처리 (GUIDE 권한 + 예외 → HTTP 상태 매핑) */
    private ResponseEntity<?> handleDecision(String authHeader, Long reservationId, boolean accept) {
        try {
            String me = userId(authHeader);
            if (!"GUIDE".equals(memberService.getRole(me))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "안내자(GUIDE)만 예약을 처리할 수 있습니다."));
            }
            reservationDTO result = accept
                    ? reservationService.accept(reservationId, me)
                    : reservationService.reject(reservationId, me);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {                 // 예약 없음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {                      // 본인 게시물 아님
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) { // 이미 처리됨/정원 부족
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
        description = """
                로그인 사용자의 예약 목록을 반환합니다. (각 항목에 status 포함)
                - **DISCOVERER**: 내가 신청한 예약 목록
                - **GUIDE**: 내 게시물에 들어온 예약 목록 (수락/거절 대상)
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = reservationDTO.class)))
    @GetMapping("/my")
    public ResponseEntity<List<reservationDTO>> getMyReservations(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader) {
        String me   = userId(authHeader);
        String role = memberService.getRole(me);
        return ResponseEntity.ok(reservationService.getMyReservations(me, role));
    }
}
