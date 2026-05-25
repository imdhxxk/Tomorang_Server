package kr.hs.after.Tomorang.Service.post;

import kr.hs.after.Tomorang.DAO.post.reservationDAO;
import kr.hs.after.Tomorang.DTO.post.reservationDTO;
import kr.hs.after.Tomorang.DTO.post.timeSlotDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class reservationServiceImp implements reservationService {

    private final reservationDAO dao;

    @Override
    @Transactional
    public void book(String memberId, reservationDTO dto) {

        // ── 1. 음수 인원 검증 ──────────────────────────────
        if (dto.getAdultCount() < 0 || dto.getChildCount() < 0) {
            throw new IllegalArgumentException("인원 수는 0명 이상이어야 합니다.");
        }

        // ── 2. 최소 1명 이상 ──────────────────────────────
        if (dto.getAdultCount() + dto.getChildCount() == 0) {
            throw new IllegalArgumentException("최소 1명 이상 예약해야 합니다.");
        }

        // ── 3. 슬롯 조회 (FOR UPDATE — 동시 예약 방지) ────
        timeSlotDTO slot = dao.selectSlotForUpdate(dto.getSlotId());
        if (slot == null) {
            throw new IllegalArgumentException("존재하지 않는 시간대입니다.");
        }
        if (!"OPEN".equals(slot.getStatus())) {
            throw new IllegalStateException("이미 마감된 시간대입니다.");
        }

        // ── 4. 잔여 인원 확인 ─────────────────────────────
        int requestTotal = dto.getAdultCount() + dto.getChildCount();
        int remaining    = slot.getMaxCapacity() - slot.getBookedCount();
        if (requestTotal > remaining) {
            throw new IllegalStateException(
                "잔여 인원이 부족합니다. (남은 자리: " + remaining + "명)"
            );
        }

        // ── 5. 예약 저장 ──────────────────────────────────
        dao.insertReservation(memberId, dto);

        // ── 6. 슬롯 인원 업데이트 (꽉 차면 자동 CLOSED) ──
        dao.updateSlotBooking(dto.getSlotId(), requestTotal);
    }

    @Override
    public List<reservationDTO> getMyReservations(String memberId) {
        return dao.selectMyReservations(memberId);
    }
}
