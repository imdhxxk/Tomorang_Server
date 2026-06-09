package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DTO.reportDTO;

public interface reportService {
    /** 신고 생성 (reporterId는 JWT에서 추출한 로그인 사용자) */
    reportDTO createReport(String reporterId, reportDTO dto);
}
