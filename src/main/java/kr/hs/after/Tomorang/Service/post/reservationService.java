package kr.hs.after.Tomorang.Service.post;

import kr.hs.after.Tomorang.DTO.post.reservationDTO;

import java.util.List;

public interface reservationService {
    void book(String memberId, reservationDTO dto);
    List<reservationDTO> getMyReservations(String memberId);
}
