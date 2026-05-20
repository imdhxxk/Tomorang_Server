package kr.hs.after.Tomorang.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import kr.hs.after.Tomorang.DTO.memberDTO;
import kr.hs.after.Tomorang.DTO.loginResponseDTO;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Encoding;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")



public class memberController {
    @Autowired
    private memberService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private tokenBlacklist blacklist;



    @Operation(summary = "회원가입 (파일 포함)")
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "dto", contentType = "application/json")
            )
    )
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> signup(
            @ModelAttribute memberDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            service.insert(dto, image);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 중 서버 오류가 발생했습니다.");
        }
    }
    @GetMapping("/profileSelect")
    public ResponseEntity<memberDTO> profileSelect(
            @RequestParam String id
    ) {
        return ResponseEntity.ok(service.profileSelect(id));
    }

    @PostMapping("/login")
    public ResponseEntity<loginResponseDTO> login(@RequestParam String id,
                                   @RequestParam String pw) {

        memberDTO dto = service.loginSelect(id);
        if (dto == null || !passwordEncoder.matches(pw, dto.getPw())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.createToken(id);

        loginResponseDTO response = new loginResponseDTO(
                token,
                "Bearer",
                dto.getId(),
                dto.getNickName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        blacklist.add(token);
        System.out.println("로그아웃");
        return ResponseEntity.ok().build();
    }

}
