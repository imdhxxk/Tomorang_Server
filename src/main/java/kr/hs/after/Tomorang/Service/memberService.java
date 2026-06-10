package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DTO.guideProfileDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface memberService {
    void insert(memberDTO dto, MultipartFile image) throws IOException;
    void updateMember(memberDTO dto, MultipartFile image) throws IOException;
    void deleteMember(String memberId);
    memberDTO profileSelect(String id);
    memberDTO loginSelect(String id);
    memberDTO findById(String id);
    memberDTO findByNickName(String nickName);
    void switchRole(String memberId, String newRole);
    String getRole(String memberId);
    List<guideProfileDTO> getPopularGuides();

    /** 발견자 역할 판별 — 표기 변형(DISCOVERER/TRAVELER/발견자) 모두 인정 */
    default boolean isDiscovererRole(String role) {
        if (role == null) return false;
        String r = role.trim();
        return r.equalsIgnoreCase("DISCOVERER")
            || r.equalsIgnoreCase("TRAVELER")
            || r.equals("발견자");
    }

    /** 안내자 역할 판별 */
    default boolean isGuideRole(String role) {
        return role != null && role.trim().equalsIgnoreCase("GUIDE");
    }
}
