package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DAO.hiddenUserDAO;
import kr.hs.after.Tomorang.DTO.hiddenUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class hiddenUserServiceImp implements hiddenUserService {

    private final hiddenUserDAO  dao;
    private final memberService  memberService;

    @Override
    public List<hiddenUserDTO> getHiddenUsers(String hiderId) {
        return dao.selectHiddenUsers(hiderId);
    }

    @Override
    @Transactional
    public void hide(String hiderId, String hiddenUserId, String reason) {
        if (hiderId.equals(hiddenUserId)) {
            throw new IllegalArgumentException("본인은 숨길 수 없습니다.");
        }
        String role = memberService.getRole(hiddenUserId);   // 없으면 null
        if (role == null) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다. (" + hiddenUserId + ")");
        }
        String r = (reason == null || reason.isBlank()) ? "MANUAL" : reason;
        dao.insertHidden(hiderId, hiddenUserId, role, r);   // INSERT IGNORE → 이미 숨김이면 그대로 성공
    }

    @Override
    @Transactional
    public void unhide(String hiderId, String hiddenUserId) {
        dao.deleteHidden(hiderId, hiddenUserId);            // 없어도 OK (멱등)
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)   // 신고 트랜잭션과 분리 → 409여도 커밋됨
    public void autoHideFromReport(String hiderId, String hiddenUserId) {
        if (hiderId == null || hiderId.equals(hiddenUserId)) return;   // 안전장치
        String role = memberService.getRole(hiddenUserId);
        dao.insertHidden(hiderId, hiddenUserId, role, "REPORT");
    }
}
