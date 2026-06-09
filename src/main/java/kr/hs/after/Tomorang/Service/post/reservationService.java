package kr.hs.after.Tomorang.Service.post;

import kr.hs.after.Tomorang.DTO.post.reservationDTO;

import java.util.List;

public interface reservationService {
    /** 예약 신청 (항상 PENDING으로 생성, 생성된 예약 반환) */
    reservationDTO book(String memberId, reservationDTO dto);

    /** 내 예약 목록 (역할에 따라: DISCOVERER=신청한 것 / GUIDE=내 게시물에 들어온 것) */
    List<reservationDTO> getMyReservations(String memberId, String role);

    /** 가이드: 예약 수락 (PENDING → CONFIRMED, 슬롯 인원 증가) */
    reservationDTO accept(Long reservationId, String guideId);

    /** 가이드: 예약 거절 (PENDING → REJECTED) */
    reservationDTO reject(Long reservationId, String guideId);
}
