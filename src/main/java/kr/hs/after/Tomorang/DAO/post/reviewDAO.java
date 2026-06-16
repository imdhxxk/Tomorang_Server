package kr.hs.after.Tomorang.DAO.post;

import kr.hs.after.Tomorang.DTO.post.reviewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface reviewDAO {
    void insertReview(@Param("memberId") String memberId, @Param("dto") reviewDTO dto);
    void insertReviewImage(@Param("reviewId") Long reviewId, @Param("url") String url);
    List<reviewDTO> selectReviewsByPostId(@Param("postId") Long postId,
                                          @Param("memberId") String memberId);
    reviewDTO selectReviewById(@Param("id") Long id, @Param("memberId") String memberId);
    List<String> selectReviewImages(@Param("reviewId") Long reviewId);

    void insertReviewLike(@Param("reviewId") Long reviewId, @Param("memberId") String memberId);
    void deleteReviewLike(@Param("reviewId") Long reviewId, @Param("memberId") String memberId);
    boolean existsReviewLike(@Param("reviewId") Long reviewId, @Param("memberId") String memberId);

    void updatePostRating(@Param("postId") Long postId);
}
