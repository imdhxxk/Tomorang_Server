package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.DTO.memberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface memberDAO {
    public void insert(memberDTO dto);
    public memberDTO profileSelect(String id);
    public memberDTO loginSelect(String id);
    public memberDTO findById(String id);
    public memberDTO findByNickName(String id);
}
