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

    /** 내(발견자) 예약 목록 조회 */
    List<reservationDTO> selectMyReservations(@Param("memberId") String memberId);

    /** 가이드의 게시물에 들어온 예약 목록 조회 */
    List<reservationDTO> selectReservationsForGuide(@Param("memberId") String memberId);

    /** 예약 단건 조회 (post 제목·일정 포함) */
    reservationDTO selectReservationById(@Param("id") Long id);

    /** 예약의 게시물 작성자(가이드) ID 조회 — 권한 검증용 */
    String selectPostAuthorByReservationId(@Param("id") Long id);

    /** 예약 상태 변경 (PENDING → CONFIRMED / REJECTED 등) */
    void updateReservationStatus(@Param("id") Long id, @Param("status") String status);
}
