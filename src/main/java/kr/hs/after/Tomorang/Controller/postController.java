package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kr.hs.after.Tomorang.DTO.post.postDTO;
import kr.hs.after.Tomorang.Service.memberService;
import kr.hs.after.Tomorang.Service.post.postService;
import kr.hs.after.Tomorang.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "게시물", description = "투어 게시물 등록 · 목록 조회 · 상세 조회 (가격·할인율·일정 포함)")
@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class postController {

    private final postService service;
    private final memberService memberService;
    private final JwtUtil jwtUtil;

    /* ───────────── 게시물 등록 ───────────── */
    @Operation(
        summary = "투어 게시물 등록",
        description = "안내자(GUIDE)가 투어 상품을 등록합니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "게시물 등록 성공")
    @ApiResponse(responseCode = "403", description = "GUIDE 역할만 게시물 등록 가능")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createPost(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "게시물 데이터 JSON", required = true)
            @RequestPart("data") postDTO dto,
            @Parameter(description = "코스 대표 이미지 목록 (선택)")
            @RequestPart(value = "courseImages", required = false) List<MultipartFile> courseImages,
            @Parameter(description = "본문 이미지 목록 (선택)")
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages) {
        try {
            String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
            String role   = memberService.getRole(userId);
            if (!"GUIDE".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "안내자(GUIDE)만 게시물을 등록할 수 있습니다."));
            }
            dto.setUser_id(userId);
            service.createPost(dto, courseImages, contentImages);
            return ResponseEntity.ok(Map.of("message", "게시물이 성공적으로 등록되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* ───────────── 게시물 목록 조회 + 통합 검색 ───────────── */
    @Operation(
        summary = "게시물 목록 조회 / 검색",
        description = """
                모든 파라미터는 선택 사항이며, 조합해서 사용할 수 있습니다.
                - **keyword** : 제목·부제목·태그에 포함된 단어로 검색
                - **city** : 도시명 필터 (예: 서울)
                - **country** : 국가명 필터 (예: 한국)
                - **userId** : 특정 안내자 게시물만 조회
                - 파라미터 없이 호출하면 전체 목록 반환
                """
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = postDTO.class)))
    @GetMapping
    public ResponseEntity<List<postDTO>> getPostList(
            @Parameter(description = "검색 키워드", example = "야경") @RequestParam(required = false) String keyword,
            @Parameter(description = "도시 필터",   example = "서울") @RequestParam(required = false) String city,
            @Parameter(description = "국가 필터",   example = "한국") @RequestParam(required = false) String country,
            @Parameter(description = "안내자 ID",   example = "guide123") @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(service.getPostList(keyword, city, country, userId));
    }

    /* ───────────── 게시물 상세 조회 ───────────── */
    @Operation(
        summary = "투어 게시물 상세 조회",
        description = """
                게시물 ID로 상세 정보를 조회합니다.
                **포함 데이터:** 코스 이미지, 본문 블록(텍스트·이미지), 다국어 태그, 예약 일정 및 시간 슬롯
                """
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = postDTO.class)))
    @ApiResponse(responseCode = "404", description = "게시물 없음")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(
            @Parameter(description = "게시물 ID", required = true, example = "1")
            @PathVariable Long id) {
        postDTO post = service.getPostDetail(id);
        if (post == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        return ResponseEntity.ok(post);
    }
}
