package kr.hs.after.Tomorang.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.servlet.http.HttpSession;
import kr.hs.after.Tomorang.DTO.LanguageDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import kr.hs.after.Tomorang.Service.memberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    @Operation(summary = "회원가입 (파일 포함)")
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "dto", contentType = "application/json")
            )
    )
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> signup(
            @RequestParam String id,
            @RequestParam String pw,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String interest,
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) String oneWord,
            @RequestParam(required = false) String langLv,
            @RequestParam(required = false) String language,  // JSON 문자열로 받기
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            memberDTO dto = new memberDTO();
            dto.setId(id);
            dto.setPw(pw);
            dto.setRole(role);
            dto.setEmail(email);
            dto.setInterest(interest);
            dto.setNickName(nickName);
            dto.setOneWord(oneWord);
            dto.setLangLv(langLv);

            // language 파싱
            if (language != null && !language.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<LanguageDTO> languageList = objectMapper.readValue(
                        language, new TypeReference<List<LanguageDTO>>() {}
                );
                dto.setLanguage(languageList);
            }

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
    public ResponseEntity<?> signup(@RequestParam("id") String id,
                                    @RequestParam("pw") String pw,
                                    HttpSession session){
        memberDTO dto = service.loginSelect(id);
        if(dto==null){
            System.out.println("실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("아이디 또는 비밀번호가 일치하지 않습니다.");
        }else{
            if(dto.getPw().equals(pw)){
                System.out.println("로그인 성공");
                session.setAttribute("s_email",id);
                return ResponseEntity.ok(dto.getNickName()+"반갑습니다.");
            }else{
                System.out.println("로그인 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        }
    }
}
