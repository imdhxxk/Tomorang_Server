package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DTO.notificationDTO;
import kr.hs.after.Tomorang.DTO.post.reservationDTO;

import java.util.List;

public interface notificationService {

    /* ── 조회/읽음 (API용) ── */
    List<notificationDTO> getMyNotifications(String receiverId);
    boolean markAsRead(Long notificationId);
    void markAllAsRead(String receiverId);
    int getUnreadCount(String receiverId);

    /* ── 서버 이벤트에서 자동 생성 (예약/리뷰 서비스가 호출) ── */
    /** 예약 확정 → 신청한 발견자에게 알림 */
    void notifyReservationConfirmed(reservationDTO reservation);
    /** 예약 거절 → 신청한 발견자에게 알림 */
    void notifyReservationRejected(reservationDTO reservation);
    /** 리뷰 작성 → 게시글 작성자(가이드)에게 알림 */
    void notifyReviewCreated(String guideId, String reviewerId,
                             Long postId, String postTitle, Long reviewId);
}
