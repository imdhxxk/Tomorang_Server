package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hs.after.Tomorang.DTO.guideProfileDTO;
import kr.hs.after.Tomorang.DTO.loginResponseDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import kr.hs.after.Tomorang.DTO.mypageDTO;
import kr.hs.after.Tomorang.Service.wishlistService;
import kr.hs.after.Tomorang.Service.memberService;
import kr.hs.after.Tomorang.jwt.JwtUtil;
import kr.hs.after.Tomorang.jwt.tokenBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "회원", description = "회원가입 · 로그인 · 프로필 조회 · 정보 수정 · 탈퇴")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class memberController {

    @Autowired private memberService   service;
    @Autowired private wishlistService wishlistService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil         jwtUtil;
    @Autowired private tokenBlacklist  blacklist;

    /* ───────────── 회원가입 ───────────── */
    @Operation(
        summary = "회원가입",
        description = "아이디, 비밀번호, 역할(GUIDE/TRAVELER), 언어 목록, 프로필 이미지를 등록합니다."
    )
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패 (비밀번호 8자 미만 / 이메일 형식 오류 / 닉네임 8자 초과 / 역할 오류)")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> signup(
            @Parameter(description = "회원 정보 JSON (id·pw·role·nickName·email·interest·languages·levels)")
            @RequestPart("dto") memberDTO dto,
            @Parameter(description = "프로필 이미지 (선택)")
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            service.insert(dto, image);
            return ResponseEntity.ok(Map.of("message", "회원가입이 성공적으로 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
        }
    }

    /* ───────────── 로그인 ───────────── */
    @Operation(
        summary = "로그인",
        description = "아이디와 비밀번호로 로그인합니다. 성공 시 JWT 토큰을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공 — JWT 토큰 반환",
            content = @Content(schema = @Schema(implementation = loginResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치")
    @PostMapping("/login")
    public ResponseEntity<loginResponseDTO> login(
            @Parameter(description = "아이디", required = true, example = "user123") @RequestParam String id,
            @Parameter(description = "비밀번호", required = true, example = "password123") @RequestParam String pw) {
        memberDTO dto = service.loginSelect(id);
        if (dto == null || !passwordEncoder.matches(pw, dto.getPw())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtUtil.createToken(id);
        return ResponseEntity.ok(new loginResponseDTO(token, "Bearer", dto.getId(), dto.getNickName()));
    }

    /* ───────────── 로그아웃 ───────────── */
    @Operation(
        summary = "로그아웃",
        description = "JWT 토큰을 블랙리스트에 추가하여 무효화합니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader) {
        blacklist.add(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok().build();
    }

    /* ───────────── 프로필 조회 ───────────── */
    @Operation(
        summary = "프로필 조회",
        description = "사용자 ID로 프로필 정보와 사용 가능 언어 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = memberDTO.class)))
    @ApiResponse(responseCode = "404", description = "사용자 없음")
    @GetMapping("/profileSelect")
    public ResponseEntity<memberDTO> profileSelect(
            @Parameter(description = "사용자 ID", required = true, example = "user123")
            @RequestParam String id) {
        memberDTO dto = service.profileSelect(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    /* ───────────── 회원 정보 수정 ───────────── */
    @Operation(
        summary = "회원 정보 수정",
        description = "JWT 토큰으로 본인 인증 후 닉네임, 한 줄 소개, 관심사, 언어, 프로필 이미지를 수정합니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PutMapping(value = "/member", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> updateMember(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "수정할 회원 정보 JSON (변경할 필드만 포함)")
            @RequestPart("dto") memberDTO dto,
            @Parameter(description = "새 프로필 이미지 (선택)")
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
            dto.setId(userId);
            service.updateMember(dto, image);
            return ResponseEntity.ok(Map.of("message", "회원 정보가 수정되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
        }
    }

    /* ───────────── 마이페이지 ───────────── */
    @Operation(
        summary = "마이페이지",
        description = "내 프로필 정보와 찜한 게시물 목록을 반환합니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/mypage")
    public ResponseEntity<mypageDTO> getMypage(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
        memberDTO profile = service.profileSelect(userId);

        mypageDTO mypage = new mypageDTO();
        mypage.setId(profile.getId());
        mypage.setNickName(profile.getNickName());
        mypage.setImage(profile.getImage());
        mypage.setOneWord(profile.getOneWord());
        mypage.setRole(profile.getRole());
        mypage.setEmail(profile.getEmail());
        mypage.setInterest(profile.getInterest());
        mypage.setLanguages(profile.getLanguages());
        mypage.setLevels(profile.getLevels());
        mypage.setNationality(profile.getNationality());
        mypage.setDefaultLanguage(profile.getDefaultLanguage());
        mypage.setAvgAnswerTime(profile.getAvgAnswerTime());
        mypage.setWishlists(wishlistService.getWishlists(userId));

        return ResponseEntity.ok(mypage);
    }

    /* ───────────── 인기 안내자 목록 ───────────── */
    @Operation(
        summary = "인기 안내자 목록",
        description = "평균 평점과 좋아요 합산 기준으로 정렬된 안내자 목록을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/guides/popular")
    public ResponseEntity<List<guideProfileDTO>> getPopularGuides() {
        return ResponseEntity.ok(service.getPopularGuides());
    }

    /* ───────────── 역할 전환 ───────────── */
    @Operation(
        summary = "역할 전환",
        description = "현재 역할을 GUIDE ↔ DISCOVERER로 전환합니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "전환 성공")
    @ApiResponse(responseCode = "400", description = "유효하지 않은 역할")
    @PutMapping("/member/role")
    public ResponseEntity<Map<String, String>> switchRole(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "새 역할 (GUIDE / DISCOVERER)", required = true)
            @RequestParam String role) {
        try {
            String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
            service.switchRole(userId, role);
            return ResponseEntity.ok(Map.of("message", "역할이 " + role.toUpperCase() + "로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* ───────────── 회원 탈퇴 ───────────── */
    @Operation(
        summary = "회원 탈퇴",
        description = "JWT 토큰으로 본인 인증 후 계정과 모든 언어 데이터를 삭제합니다. 토큰도 즉시 무효화됩니다.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "탈퇴 성공")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @DeleteMapping("/member")
    public ResponseEntity<Map<String, String>> deleteMember(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
            service.deleteMember(userId);
            blacklist.add(authHeader.replace("Bearer ", ""));
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
        }
    }
}
