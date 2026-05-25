package kr.hs.after.Tomorang.Service.post;

import kr.hs.after.Tomorang.DTO.post.reviewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface reviewService {
    void createReview(String memberId, reviewDTO dto, List<MultipartFile> images) throws IOException;
    List<reviewDTO> getReviews(Long postId, String memberId);
    void likeReview(Long reviewId, String memberId);
    void unlikeReview(Long reviewId, String memberId);
}
