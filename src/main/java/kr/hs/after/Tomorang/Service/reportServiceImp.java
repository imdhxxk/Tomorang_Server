package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DAO.reportDAO;
import kr.hs.after.Tomorang.DAO.post.postDAO;
import kr.hs.after.Tomorang.DTO.reportDTO;
import kr.hs.after.Tomorang.DTO.post.postDTO;
import kr.hs.after.Tomorang.exception.DuplicateReportException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class reportServiceImp implements reportService {

    private final reportDAO dao;
    private final postDAO   postDAO;

    private static final Set<String> VALID_REASONS =
            Set.of("SPAM", "INAPPROPRIATE", "FRAUD", "HARASSMENT", "OTHER");

    @Override
    @Transactional
    public reportDTO createReport(String reporterId, reportDTO dto) {

        // 1. 입력 검증
        if (dto.getTargetType() == null || dto.getTargetId() == null) {
            throw new IllegalArgumentException("targetType과 targetId는 필수입니다.");
        }
        if (dto.getReason() == null || !VALID_REASONS.contains(dto.getReason())) {
            throw new IllegalArgumentException("reason은 다음 중 하나여야 합니다: " + VALID_REASONS);
        }
        if (!"POST".equals(dto.getTargetType())) {
            throw new IllegalArgumentException("현재 지원하는 신고 대상은 POST 뿐입니다.");
        }

        // 2. 게시물 존재 확인 (없으면 404)
        postDTO post = postDAO.selectPostById(dto.getTargetId());
        if (post == null) {
            throw new NoSuchElementException("존재하지 않는 게시물입니다. (targetId=" + dto.getTargetId() + ")");
        }

        // 3. 본인 게시물 신고 불가 (400)
        if (reporterId.equals(post.getUser_id())) {
            throw new IllegalArgumentException("본인 게시물은 신고할 수 없습니다.");
        }

        // 4. 중복 신고 차단 (409)
        if (dao.existsReport(reporterId, dto.getTargetType(), dto.getTargetId())) {
            throw new DuplicateReportException("이미 신고한 게시물입니다.");
        }

        // 5. 저장 (status = PENDING)
        dto.setReporterId(reporterId);
        dao.insertReport(dto);

        // 6. 생성된 신고 반환
        return dao.selectReportById(dto.getReportId());
    }
}
