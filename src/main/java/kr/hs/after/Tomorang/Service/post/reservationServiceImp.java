package kr.hs.after.Tomorang.Service.post;

import kr.hs.after.Tomorang.DAO.post.reservationDAO;
import kr.hs.after.Tomorang.DTO.post.reservationDTO;
import kr.hs.after.Tomorang.DTO.post.timeSlotDTO;
import kr.hs.after.Tomorang.Service.chatRoomService;
import kr.hs.after.Tomorang.Service.notificationService;
import kr.hs.after.Tomorang.model.chatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class reservationServiceImp implements reservationService {

    private final reservationDAO      dao;
    private final chatRoomService     chatRoomService;
    private final notificationService notificationService;

    /* ───────────── 예약 신청 (항상 PENDING) ───────────── */
    @Override
    @Transactional
    public reservationDTO book(String memberId, reservationDTO dto) {

        // 1. 음수 인원 검증
        if (dto.getAdultCount() < 0 || dto.getChildCount() < 0) {
            throw new IllegalArgumentException("인원 수는 0명 이상이어야 합니다.");
        }
        // 2. 최소 1명 이상
        if (dto.getAdultCount() + dto.getChildCount() == 0) {
            throw new IllegalArgumentException("최소 1명 이상 예약해야 합니다.");
        }
        // 3. 슬롯 존재 + 오픈 여부 확인 (인원 증가는 수락 시에만!)
        timeSlotDTO slot = dao.selectSlotForUpdate(dto.getSlotId());
        if (slot == null) {
            throw new IllegalArgumentException("존재하지 않는 시간대입니다.");
        }
        if (!"OPEN".equals(slot.getStatus())) {
            throw new IllegalStateException("이미 마감된 시간대입니다.");
        }

        // 4. 예약 저장 (status = PENDING, booked_count 변동 없음)
        dao.insertReservation(memberId, dto);

        // 5. 생성된 예약 반환
        return dao.selectReservationById(dto.getId());
    }

    /* ───────────── 내 예약 목록 (역할별) ───────────── */
    @Override
    public List<reservationDTO> getMyReservations(String memberId, String role) {
        if ("GUIDE".equals(role)) {
            return dao.selectReservationsForGuide(memberId);
        }
        return dao.selectMyReservations(memberId);
    }

    /* ───────────── 예약 수락 (PENDING → CONFIRMED) ───────────── */
    @Override
    @Transactional
    public reservationDTO accept(Long reservationId, String guideId) {
        reservationDTO r = loadAndAuthorize(reservationId, guideId);

        // PENDING만 수락 가능
        if (!"PENDING".equals(r.getStatus())) {
            throw new IllegalStateException("이미 처리된 예약입니다. (현재 상태: " + r.getStatus() + ")");
        }

        int requestTotal = r.getAdultCount() + r.getChildCount();

        // 슬롯 잠금 후 정원 확인 (정원 초과 방지)
        timeSlotDTO slot = dao.selectSlotForUpdate(r.getSlotId());
        if (slot == null) {
            throw new IllegalStateException("시간대 정보를 찾을 수 없습니다.");
        }
        int remaining = slot.getMaxCapacity() - slot.getBookedCount();
        if (requestTotal > remaining) {
            throw new IllegalStateException("잔여 인원이 부족합니다. (남은 자리: " + remaining + "명)");
        }

        // 상태 변경 + 슬롯 인원 증가 (꽉 차면 자동 CLOSED)
        dao.updateReservationStatus(reservationId, "CONFIRMED");
        dao.updateSlotBooking(r.getSlotId(), requestTotal);

        // 발견자(requester) ↔ 가이드(guideId) 채팅방 생성 또는 재사용
        chatRoom room = chatRoomService.getOrCreateChatRoom(r.getMemberId(), guideId);

        reservationDTO result = dao.selectReservationById(reservationId);
        result.setChatRoomId(room.getRoomId());   // 응답에 채팅방 ID 포함

        // 발견자에게 "예약 확정" 알림 (실패해도 수락 자체는 성공 처리)
        try { notificationService.notifyReservationConfirmed(result); }
        catch (Exception e) { log.warn("예약 확정 알림 생성 실패: {}", e.getMessage()); }

        return result;
    }

    /* ───────────── 예약 거절 (PENDING → REJECTED) ───────────── */
    @Override
    @Transactional
    public reservationDTO reject(Long reservationId, String guideId) {
        reservationDTO r = loadAndAuthorize(reservationId, guideId);

        if (!"PENDING".equals(r.getStatus())) {
            throw new IllegalStateException("이미 처리된 예약입니다. (현재 상태: " + r.getStatus() + ")");
        }

        // 거절은 슬롯 인원 변동 없음
        dao.updateReservationStatus(reservationId, "REJECTED");

        reservationDTO result = dao.selectReservationById(reservationId);

        // 발견자에게 "예약 거절" 알림 (실패해도 거절 자체는 성공 처리)
        try { notificationService.notifyReservationRejected(result); }
        catch (Exception e) { log.warn("예약 거절 알림 생성 실패: {}", e.getMessage()); }

        return result;
    }

    /* ───────────── 공통: 예약 로드 + 가이드 권한 검증 ───────────── */
    private reservationDTO loadAndAuthorize(Long reservationId, String guideId) {
        reservationDTO r = dao.selectReservationById(reservationId);
        if (r == null) {
            throw new NoSuchElementException("예약을 찾을 수 없습니다.");   // → 404
        }
        String owner = dao.selectPostAuthorByReservationId(reservationId);
        if (owner == null || !owner.equals(guideId)) {
            throw new SecurityException("본인 게시물의 예약만 처리할 수 있습니다."); // → 403
        }
        return r;
    }
}
