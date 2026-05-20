package kr.hs.after.Tomorang.Service.post;

import kr.hs.after.Tomorang.DTO.post.postDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface postService {
    void createPost(postDTO dto,
                    List<MultipartFile> courseImages,
                    List<MultipartFile> contentImages) throws IOException;

    List<postDTO> getPostList(String city, String country, String userId);

    postDTO getPostDetail(Long postId);
}
