package kr.hs.after.Tomorang.DAO.post;

import kr.hs.after.Tomorang.DTO.post.contentBlockDTO;
import kr.hs.after.Tomorang.DTO.post.postDTO;
import kr.hs.after.Tomorang.DTO.post.scheduleDTO;
import kr.hs.after.Tomorang.DTO.post.tagDTO;
import kr.hs.after.Tomorang.DTO.post.timeSlotDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface postDAO {
    void postInsert(postDTO dto);
    void insertPostImage(@Param("post_id") Long post_id, @Param("url") String url);
    void insertPostTag(@Param("post_id") Long post_id, @Param("tag") tagDTO tag);
    void insertPostContent(@Param("post_id") Long post_id, @Param("content") contentBlockDTO content);
    void insertPostSchedule(Map<String, Object> map);
    void insertTimeSlot(@Param("scheduleId") Long scheduleId, @Param("slot") timeSlotDTO slot);

    // 통합 조회·검색 (모든 파라미터 선택). viewerId가 있으면 그 사용자가 숨긴 작성자의 글을 제외
    List<postDTO> selectPosts(@Param("keyword")  String keyword,
                              @Param("city")     String city,
                              @Param("country")  String country,
                              @Param("userId")   String userId,
                              @Param("viewerId") String viewerId);
    postDTO selectPostById(@Param("postId") Long postId);

    // 게시물 수정 (스칼라 필드)
    void updatePost(postDTO dto);

    // 게시물 삭제 (FK 자식 정리 → posts 삭제 시 images/contents/tags/schedules는 CASCADE)
    void deleteReviewLikesByPost(@Param("postId") Long postId);
    void deleteReviewImagesByPost(@Param("postId") Long postId);
    void deleteReviewsByPost(@Param("postId") Long postId);
    void deleteReservationsByPost(@Param("postId") Long postId);
    void deleteWishlistsByPost(@Param("postId") Long postId);
    void deletePostById(@Param("postId") Long postId);
    List<String> selectPostImages(@Param("postId") Long postId);
    List<contentBlockDTO> selectPostContents(@Param("postId") Long postId);
    List<tagDTO> selectPostTags(@Param("postId") Long postId);
    List<scheduleDTO> selectSchedulesByPostId(@Param("postId") Long postId);
    List<timeSlotDTO> selectTimeSlotsByScheduleId(@Param("scheduleId") Long scheduleId);
}
