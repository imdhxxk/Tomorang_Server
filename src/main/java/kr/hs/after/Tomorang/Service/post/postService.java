package kr.hs.after.Tomorang.Service.post;

import kr.hs.after.Tomorang.DTO.post.postDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface postService {
    void createPost(postDTO dto,
                    List<MultipartFile> courseImages,
                    List<MultipartFile> contentImages) throws IOException;

    // viewerId(로그인 사용자)가 있으면 그가 숨긴 작성자의 글을 제외
    List<postDTO> getPostList(String keyword, String city, String country, String userId, String viewerId);

    postDTO getPostDetail(Long postId);

    /** 스칼라 필드만 조회 (작성자 검증용) */
    postDTO findPostById(Long postId);

    /** 게시물 수정 (스칼라 필드) */
    void updatePost(postDTO dto);

    /** 게시물 삭제 (연관 데이터 정리 포함) */
    void deletePost(Long postId);
}
