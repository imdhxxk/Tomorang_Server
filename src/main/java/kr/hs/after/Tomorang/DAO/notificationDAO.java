package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.DTO.notificationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface notificationDAO {

    /** 알림 저장 (성공 시 dto.notificationId 채워짐) */
    void insertNotification(notificationDTO dto);

    /** 받는 사람 기준 알림 목록 (최신순) */
    List<notificationDTO> selectByReceiver(@Param("receiverId") String receiverId);

    /** 단건 읽음 처리 (영향 행 수 반환) */
    int markAsRead(@Param("notificationId") Long notificationId);

    /** 받는 사람의 모든 알림 읽음 처리 */
    void markAllAsRead(@Param("receiverId") String receiverId);

    /** 안 읽은 알림 개수 */
    int countUnread(@Param("receiverId") String receiverId);
}
