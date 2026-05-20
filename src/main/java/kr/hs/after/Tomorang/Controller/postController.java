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

    /**
     * 게시물 등록
     * POST /api/post
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPost(
            @RequestPart("data") postDTO dto,
            @RequestPart(value = "courseImages", required = false) List<MultipartFile> courseImages,
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

    /**
     * 게시물 목록 조회 (필터: city, country, userId)
     * GET /api/post?city=서울&country=한국&userId=guide1
     */
    @GetMapping
    public ResponseEntity<List<postDTO>> getPostList(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String userId
    ) {
        List<postDTO> posts = service.getPostList(city, country, userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * 게시물 상세 조회 (이미지, 본문, 태그, 일정 포함)
     * GET /api/post/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id) {
        postDTO post = service.getPostDetail(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("게시물을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(post);
    }
}
