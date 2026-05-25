package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.DTO.LanguageType;
import kr.hs.after.Tomorang.DTO.guideProfileDTO;
import kr.hs.after.Tomorang.DTO.languageDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface memberDAO {
    void insert(memberDTO dto);
    void insertLanguage(@Param("memberId") String memberId,
                        @Param("language") LanguageType language,
                        @Param("level") int level);
    void updateMember(memberDTO dto);
    void deleteMember(@Param("memberId") String memberId);
    void deleteLanguages(@Param("memberId") String memberId); // 언어 삭제 (탈퇴/수정 시)
    memberDTO loginSelect(String id);
    memberDTO findById(String id);
    memberDTO findByNickName(String nickName);
    memberDTO selectMember(String id);
    List<languageDTO> selectLanguages(@Param("memberId") String memberId);
    void updateRole(@Param("memberId") String memberId, @Param("role") String role);
    String selectRole(@Param("memberId") String memberId);
    List<guideProfileDTO> selectPopularGuides();
}
