package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.DTO.reportDTO;
import kr.hs.after.Tomorang.Service.reportService;
import kr.hs.after.Tomorang.exception.DuplicateReportException;
import kr.hs.after.Tomorang.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "신고", description = "게시물 신고 생성 (로그인 필요)")
@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class reportController {

    private final reportService reportService;
    private final JwtUtil       jwtUtil;

    @Operation(
        summary = "게시물 신고",
        description = """
                로그인한 사용자가 게시물을 신고합니다. (신고 직후 상태 PENDING)

                **정책**
                - 로그인한 사용자만 신고 가능 (미인증 시 401)
                - 본인 게시물은 신고 불가 (400)
                - 같은 게시물을 중복 신고하면 409 Conflict
                - 존재하지 않는 게시물이면 404

                **reason**: SPAM / INAPPROPRIATE / FRAUD / HARASSMENT / OTHER
                """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "신고 생성 성공",
            content = @Content(schema = @Schema(implementation = reportDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 / 본인 게시물 신고")
    @ApiResponse(responseCode = "401", description = "로그인 필요 / 토큰 무효")
    @ApiResponse(responseCode = "404", description = "게시물 없음")
    @ApiResponse(responseCode = "409", description = "이미 신고한 게시물")
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody reportDTO dto) {

        // 인증 확인
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }
        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "유효하지 않은 토큰입니다."));
        }
        String reporterId = jwtUtil.getUserId(token);

        try {
            reportDTO created = reportService.createReport(reporterId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (NoSuchElementException e) {                 // 게시물 없음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DuplicateReportException e) {               // 중복 신고
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {               // 검증 실패 / 본인 신고
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
