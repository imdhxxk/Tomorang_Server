package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DTO.hiddenUserDTO;

import java.util.List;

public interface hiddenUserService {

    /** 내가 숨긴 사용자 목록 (프로필 포함) */
    List<hiddenUserDTO> getHiddenUsers(String hiderId);

    /** 수동 숨김 (본인 불가, 대상 없으면 예외, 이미 숨김이면 그대로 성공) */
    void hide(String hiderId, String hiddenUserId, String reason);

    /** 숨김 해제 (멱등) */
    void unhide(String hiderId, String hiddenUserId);

    /** 신고 성공 시 자동 숨김 — 별도 트랜잭션으로 독립 커밋 (신고 409여도 숨김은 유지) */
    void autoHideFromReport(String hiderId, String hiddenUserId);
}
