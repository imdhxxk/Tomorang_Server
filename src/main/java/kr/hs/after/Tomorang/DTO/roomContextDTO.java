package kr.hs.after.Tomorang.DTO;

import lombok.Data;

/**
 * 채팅방 ↔ 예약/게시글 연결 정보.
 * 두 사용자 사이의 (가장 최근/확정) 예약을 통해 게시글 컨텍스트를 끌어온다.
 * roomId(UUID)와 postId(숫자)를 분리해서 내려주기 위한 보조 DTO.
 */
@Data
public class roomContextDTO {
    private Long   reservationId;
    private Long   postId;
    private String postTitle;
    private String thumbnailUrl;
}
