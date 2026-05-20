package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.DTO.memberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface memberDAO {
    public void insert(memberDTO dto);
    public void insertLanguage(String memberId, String language, String level);
    public void updateMember(String memberId);
    public void deleteMember(String memberId);
    public memberDTO loginSelect(String id);
    public memberDTO findById(String id);
    public memberDTO findByNickName(String id);
    public memberDTO selectMember(String id);
    public memberDTO selectLanguages(String id);
}
