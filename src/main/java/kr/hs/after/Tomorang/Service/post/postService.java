package kr.hs.after.Tomorang.Service.post;


import kr.hs.after.Tomorang.DTO.post.postDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface postService {
/**
        * 게시물 등록 (이미지 업로드 포함)
     */
    public void createPost(postDTO dto,
                    List<MultipartFile> courseImages,
                    List<MultipartFile> contentImages) throws IOException;
}
// 나중에 필요한 기능들을 여기에 미리 정의할 수 있어요.
// PostResponseDTO getPost(Long postId);
// void deletePost(Long postId);}
