package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DTO.memberDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface memberService {
    public void insert(memberDTO dto, MultipartFile image) throws IOException;
    public memberDTO profileSelect(String id);
    public memberDTO loginSelect(String id);
    public memberDTO findById(String id);
    public memberDTO findByNickName(String nickName);
}
