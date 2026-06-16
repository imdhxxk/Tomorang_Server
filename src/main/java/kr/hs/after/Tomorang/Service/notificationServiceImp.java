package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DAO.notificationDAO;
import kr.hs.after.Tomorang.DTO.notificationDTO;
import kr.hs.after.Tomorang.DTO.post.reservationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class notificationServiceImp implements notificationService {

    private final notificationDAO dao;

    /* ───────────── 조회/읽음 ───────────── */
    @Override
    public List<notificationDTO> getMyNotifications(String receiverId) {
        return dao.selectByReceiver(receiverId);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long notificationId) {
        return dao.markAsRead(notificationId) > 0;
    }

    @Override
    @Transactional
    public void markAllAsRead(String receiverId) {
        dao.markAllAsRead(receiverId);
    }

    @Override
    public int getUnreadCount(String receiverId) {
        return dao.countUnread(receiverId);
    }

    /* ───────────── 예약 신청 알림 (예약 트랜잭션 내에서 함께 저장 — REQUIRES_NEW 아님) ───────────── */
    @Override
    public void notifyReservationRequested(reservationDTO r) {
        // 게시글 작성자(가이드)에게. (작성자와 신청자가 같으면 알림 생략)
        if (r.getGuideId() == null || r.getGuideId().equals(r.getMemberId())) return;
        notificationDTO n = base(r.getGuideId(), r.getMemberId(), "RESERVATION_REQUESTED",
                "새 예약 요청이 왔어요!",
                "[" + r.getPostTitle() + "]에 새로운 예약 요청이 도착했어요.");
        n.setPostId(r.getPostId());
        n.setReservationId(r.getId());
        dao.insertNotification(n);
    }

    /* ───────────── 자동 생성 (예약/리뷰 트랜잭션과 분리: REQUIRES_NEW) ───────────── */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyReservationConfirmed(reservationDTO r) {
        String when = formatDate(r.getSlotDate());
        String time = r.getSlotTime() != null ? " " + r.getSlotTime() : "";
        notificationDTO n = base(r.getMemberId(), r.getGuideId(), "RESERVATION_CONFIRMED",
                "예약이 확정되었어요!",
                "[" + r.getPostTitle() + "] " + when + time + " 예약을 확정하셨어요.");
        n.setPostId(r.getPostId());
        n.setReservationId(r.getId());
        dao.insertNotification(n);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyReservationRejected(reservationDTO r) {
        notificationDTO n = base(r.getMemberId(), r.getGuideId(), "RESERVATION_REJECTED",
                "예약이 거절되었어요",
                "[" + r.getPostTitle() + "] 예약이 거절되었어요.");
        n.setPostId(r.getPostId());
        n.setReservationId(r.getId());
        dao.insertNotification(n);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyReviewCreated(String guideId, String reviewerId,
                                    Long postId, String postTitle, Long reviewId) {
        if (guideId == null || guideId.equals(reviewerId)) return;   // 본인 글에 본인 리뷰면 알림 X
        notificationDTO n = base(guideId, reviewerId, "REVIEW_CREATED",
                "새 리뷰가 도착했어요!",
                "[" + postTitle + "]에 새로운 리뷰가 작성되었어요.");
        n.setPostId(postId);
        n.setReviewId(reviewId);
        dao.insertNotification(n);
    }

    /* ───────────── 내부 헬퍼 ───────────── */
    private notificationDTO base(String receiverId, String senderId, String type,
                                 String title, String message) {
        notificationDTO n = new notificationDTO();
        n.setReceiverId(receiverId);
        n.setSenderId(senderId);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setIsRead(false);
        return n;
    }

    /** "2026-06-01" → "2026년 06월 01일" (파싱 실패 시 원본 그대로) */
    private String formatDate(String slotDate) {
        if (slotDate == null || slotDate.isBlank()) return "";
        try {
            LocalDate d = LocalDate.parse(slotDate.trim());
            return d.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        } catch (Exception e) {
            return slotDate;
        }
    }
}
