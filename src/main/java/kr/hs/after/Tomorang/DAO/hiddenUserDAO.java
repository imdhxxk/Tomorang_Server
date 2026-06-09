package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.DTO.hiddenUserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface hiddenUserDAO {

    /** 숨김 추가 (이미 있으면 무시 — INSERT IGNORE) */
    void insertHidden(@Param("hiderId")      String hiderId,
                      @Param("hiddenUserId") String hiddenUserId,
                      @Param("role")         String role,
                      @Param("reason")       String reason);

    /** 숨김 해제 */
    void deleteHidden(@Param("hiderId")      String hiderId,
                      @Param("hiddenUserId") String hiddenUserId);

    /** 이미 숨김 상태인지 */
    boolean existsHidden(@Param("hiderId")      String hiderId,
                         @Param("hiddenUserId") String hiddenUserId);

    /** 내가 숨긴 사용자 목록 (프로필 포함, 최신순) */
    List<hiddenUserDTO> selectHiddenUsers(@Param("hiderId") String hiderId);
}
