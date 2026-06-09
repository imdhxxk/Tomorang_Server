package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.DTO.reportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface reportDAO {

    /** 신고 저장 (성공 시 dto.reportId에 생성된 ID 채워짐) */
    void insertReport(reportDTO dto);

    /** 동일 신고자·대상 중복 여부 (중복 신고 차단용) */
    boolean existsReport(@Param("reporterId") String reporterId,
                         @Param("targetType") String targetType,
                         @Param("targetId")   Long   targetId);

    /** 신고 단건 조회 */
    reportDTO selectReportById(@Param("reportId") Long reportId);
}
