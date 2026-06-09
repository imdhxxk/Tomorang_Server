package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.DTO.hiddenUserDTO;
import kr.hs.after.Tomorang.Service.hiddenUserService;
import kr.hs.after.Tomorang.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "숨김 사용자", description = "사용자 숨김/숨김해제 및 숨긴 목록 (신고 시 자동 숨김 포함)")
@RestController
@RequestMapping("/api/hidden-users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class hiddenUserController {

    private final hiddenUserService hiddenUserService;
    private final JwtUtil           jwtUtil;

    /** 토큰에서 사용자 ID 추출 (무효/누락 시 null) */
    private String userIdOrNull(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.validateToken(token) ? jwtUtil.getUserId(token) : null;
    }

    /* ───────────── 숨긴 사용자 목록 ───────────── */
    @Operation(
        summary = "숨긴 사용자 목록 조회",
        description = """
                로그인 사용자가 숨긴 사용자 목록을 반환합니다.
                (발견자→숨긴 안내자, 안내자→숨긴 발견자가 자연히 포함)
                프로필 이미지·닉네임·한 줄 소개 포함.
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = hiddenUserDTO.class)))
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @GetMapping
    public ResponseEntity<?> list(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String me = userIdOrNull(authHeader);
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        List<hiddenUserDTO> result = hiddenUserService.getHiddenUsers(me);
        return ResponseEntity.ok(result);
    }

    /* ───────────── 사용자 숨김 ───────────── */
    @Operation(
        summary = "사용자 숨김",
        description = """
                특정 사용자를 숨깁니다. (본인 숨김 불가, 이미 숨김 상태면 그대로 성공)
                `reason`은 선택 쿼리 파라미터입니다 (생략 시 MANUAL). 요청 바디 없이 호출 가능.
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "숨김 성공 (`{hiddenUserId, hidden:true}`)")
    @ApiResponse(responseCode = "400", description = "본인 숨김 불가")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @ApiResponse(responseCode = "404", description = "대상 사용자 없음")
    @PostMapping("/{hiddenUserId}")
    public ResponseEntity<?> hide(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "숨길 사용자 ID", required = true, example = "guide123")
            @PathVariable String hiddenUserId,
            @Parameter(description = "숨김 사유 (선택, 기본 MANUAL)", example = "MANUAL")
            @RequestParam(value = "reason", required = false) String reason) {
        String me = userIdOrNull(authHeader);
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        try {
            hiddenUserService.hide(me, hiddenUserId, reason);
            Map<String, Object> resp = new HashMap<>();
            resp.put("hiddenUserId", hiddenUserId);
            resp.put("hidden", true);
            return ResponseEntity.ok(resp);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /* ───────────── 사용자 숨김 해제 ───────────── */
    @Operation(
        summary = "사용자 숨김 해제",
        description = "숨김을 해제합니다. (없는 데이터여도 204로 통일)",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "해제 완료")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @DeleteMapping("/{hiddenUserId}")
    public ResponseEntity<?> unhide(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "숨김 해제할 사용자 ID", required = true, example = "guide123")
            @PathVariable String hiddenUserId) {
        String me = userIdOrNull(authHeader);
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        hiddenUserService.unhide(me, hiddenUserId);
        return ResponseEntity.noContent().build();   // 204
    }
}
