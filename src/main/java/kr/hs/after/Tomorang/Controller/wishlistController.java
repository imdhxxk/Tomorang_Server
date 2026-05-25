package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.Service.memberService;
import kr.hs.after.Tomorang.Service.wishlistService;
import kr.hs.after.Tomorang.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "찜", description = "게시물 찜 추가 · 취소 (발견자 전용)")
@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class wishlistController {

    private final wishlistService wishlistService;
    private final memberService   memberService;
    private final JwtUtil         jwtUtil;

    /* ───────────── 찜 추가 ───────────── */
    @Operation(
        summary = "찜 추가",
        description = "발견자(DISCOVERER)가 게시물을 찜 목록에 추가합니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "찜 추가 성공")
    @ApiResponse(responseCode = "403", description = "DISCOVERER만 사용 가능")
    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, String>> add(
            @PathVariable Long postId,
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
        if (!"DISCOVERER".equals(memberService.getRole(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "발견자(DISCOVERER)만 찜할 수 있습니다."));
        }
        wishlistService.addWishlist(userId, postId);
        return ResponseEntity.ok(Map.of("message", "찜 목록에 추가되었습니다."));
    }

    /* ───────────── 찜 취소 ───────────── */
    @Operation(
        summary = "찜 취소",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> remove(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
        wishlistService.removeWishlist(userId, postId);
        return ResponseEntity.ok(Map.of("message", "찜 목록에서 제거되었습니다."));
    }
}
