package kr.hs.after.Tomorang.DAO.post;

import kr.hs.after.Tomorang.DTO.post.reservationDTO;
import kr.hs.after.Tomorang.DTO.post.timeSlotDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface reservationDAO {

    /** 슬롯 조회 (비관적 잠금 — 동시 예약 방지) */
    timeSlotDTO selectSlotForUpdate(@Param("slotId") String slotId);

    /** 예약 저장 */
    void insertReservation(@Param("memberId") String memberId,
                           @Param("dto") reservationDTO dto);

    /** 슬롯 예약 인원 업데이트 (꽉 차면 자동 CLOSED) */
    void updateSlotBooking(@Param("slotId") String slotId,
                           @Param("count") int count);

    /** 내 예약 목록 조회 */
    List<reservationDTO> selectMyReservations(@Param("memberId") String memberId);
}
