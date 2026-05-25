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

    // 통합 조회·검색 (모든 파라미터 선택)
    List<postDTO> selectPosts(@Param("keyword") String keyword,
                              @Param("city")    String city,
                              @Param("country") String country,
                              @Param("userId")  String userId);
    postDTO selectPostById(@Param("postId") Long postId);
    List<String> selectPostImages(@Param("postId") Long postId);
    List<contentBlockDTO> selectPostContents(@Param("postId") Long postId);
    List<tagDTO> selectPostTags(@Param("postId") Long postId);
    List<scheduleDTO> selectSchedulesByPostId(@Param("postId") Long postId);
    List<timeSlotDTO> selectTimeSlotsByScheduleId(@Param("scheduleId") Long scheduleId);
}
