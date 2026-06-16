package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.DTO.post.reviewDTO;
import kr.hs.after.Tomorang.Service.post.reviewService;
import kr.hs.after.Tomorang.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "리뷰", description = "리뷰 작성 · 조회 · 좋아요")
@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class reviewController {

    private final reviewService service;
    private final JwtUtil       jwtUtil;

    /* ───────────── 리뷰 작성 ───────────── */
    @Operation(
        summary = "리뷰 작성",
        description = "별점(1~5), 내용, 사진으로 리뷰를 작성합니다. 작성 시 게시물 평점이 자동 업데이트됩니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "작성 성공 — 생성된 리뷰(reviewDTO, createdAt 포함) 반환")
    @ApiResponse(responseCode = "400", description = "별점 범위 오류")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReview(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "리뷰 정보 JSON (postId, rating, content)")
            @RequestPart("dto") reviewDTO dto,
            @Parameter(description = "리뷰 이미지 (선택, 여러 장 가능)")
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
            reviewDTO created = service.createReview(userId, dto, images);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* ───────────── 리뷰 목록 조회 ───────────── */
    @Operation(
        summary = "게시물 리뷰 목록",
        description = "게시물의 리뷰 목록을 조회합니다. 로그인 시 내가 좋아요 눌렀는지 여부도 표시됩니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<reviewDTO>> getReviews(
            @PathVariable Long postId,
            @Parameter(description = "Bearer JWT 토큰 (선택 — 있으면 liked 여부 표시)")
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String userId = null;
        if (authHeader != null) {
            userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
        }
        return ResponseEntity.ok(service.getReviews(postId, userId));
    }

    /* ───────────── 좋아요 ───────────── */
    @Operation(
        summary = "리뷰 좋아요",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Map<String, String>> like(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
        service.likeReview(reviewId, userId);
        return ResponseEntity.ok(Map.of("message", "좋아요를 눌렀습니다."));
    }

    /* ───────────── 좋아요 취소 ───────────── */
    @Operation(
        summary = "리뷰 좋아요 취소",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<Map<String, String>> unlike(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
        service.unlikeReview(reviewId, userId);
        return ResponseEntity.ok(Map.of("message", "좋아요를 취소했습니다."));
    }
}
