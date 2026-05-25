package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DTO.memberDTO;


public interface memberService {
    public void insert(memberDTO dto);
    public memberDTO profileSelect(String id);
    public memberDTO loginSelect(String id);
    memberDTO findById(String id);
    memberDTO findByNickName(String nickName);
}
