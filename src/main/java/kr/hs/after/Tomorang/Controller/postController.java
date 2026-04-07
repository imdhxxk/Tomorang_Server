package kr.hs.after.Tomorang.Controller;

import kr.hs.after.Tomorang.DTO.post.postDTO;
import kr.hs.after.Tomorang.Service.post.postService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class postController {

    private final postService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPost(
            // 텍스트 정보 (JSON 형태의 문자열로 받을 수도 있지만,/
            // Swagger 호환성을 위해 @RequestPart로 지정)
            @RequestPart("data") postDTO dto,
            // 코스 상단 이미지들
            @RequestPart(value = "courseImages", required = false) List<MultipartFile> courseImages,
            // 본문 블록에 들어갈 이미지들
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages
    ) {
        try {
            service.createPost(dto, courseImages, contentImages);
            return ResponseEntity.ok("게시물이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("오류 발생: " + e.getMessage());
        }
    }



}
