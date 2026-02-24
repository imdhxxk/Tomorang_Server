package kr.hs.after.Tomorang.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpSession;
import kr.hs.after.Tomorang.DTO.memberDTO;
import kr.hs.after.Tomorang.Service.memberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")



public class memberController {
    @Autowired
    private memberService service;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody memberDTO dto) {

        // 1. 아이디 먼저 체크
        if (service.findById(dto.getId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
        }

        // 2. 닉네임 체크
        if (service.findByNickName(dto.getNickName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 닉네임입니다.");
        }

        // 3. 둘 다 통과하면 가입!
        service.insert(dto);
        return ResponseEntity.ok("회원가입 완료");
    }
    @GetMapping("/login")
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
    @Operation(summary = "회원 프로필 등록", description = "회원 기본 정보와 프로필 이미지를 업로드합니다.")
    @PostMapping(
            value = "/members/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createProfile(

            @Parameter(description = "회원 정보 JSON")
            @RequestPart("member") MemberProfileRequest request,

            @Parameter(description = "프로필 이미지 파일")
            @RequestPart("image") MultipartFile image

    ) throws IOException {

        // 1️⃣ 업로드 폴더 생성 (없으면)
        String uploadDir = "src/main/resources/static/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2️⃣ 파일명 생성
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        File saveFile = new File(uploadDir + fileName);

        // 3️⃣ 파일 저장
        image.transferTo(saveFile);

        // 4️⃣ DB에 저장할 경로
        String imagePath = "/uploads/" + fileName;

        // 5️⃣ DB 매핑용 객체 생성 (MyBatis용)
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setNickname(request.getNickname());
        member.setProfileImage(imagePath);

        // 6️⃣ MyBatis Mapper 호출
        memberMapper.insertMember(member);

        return ResponseEntity.ok(member);
    }


}
