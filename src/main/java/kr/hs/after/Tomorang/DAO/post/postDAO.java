package kr.hs.after.Tomorang.DAO.post;

import kr.hs.after.Tomorang.DTO.post.contentBlockDTO;
import kr.hs.after.Tomorang.DTO.post.postDTO;
import kr.hs.after.Tomorang.DTO.post.tagDTO;
import kr.hs.after.Tomorang.DTO.post.timeSlotDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface postDAO {
    public void postInsert(postDTO dto);
    public void insertPostImage(@Param("post_id") Long post_id, @Param("url") String url);
    void insertPostTag(@Param("post_id") Long post_id, @Param("tag") tagDTO tag);

    // 4. post_contents (본문 블록) 저장
    void insertPostContent(@Param("post_id") Long post_id, @Param("content") contentBlockDTO content);

    // 5. post_schedules 저장
    void insertPostSchedule(Map<String, Object> map); // schedule_id를 반환받기 위해 Map 사용

    // 6. time_slots 저장
    void insertTimeSlot(@Param("scheduleId") Long scheduleId, @Param("slot") timeSlotDTO slot);
}
