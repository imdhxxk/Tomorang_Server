package kr.hs.after.Tomorang.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hs.after.Tomorang.DAO.memberDAO;
import kr.hs.after.Tomorang.DTO.LanguageType;
import kr.hs.after.Tomorang.DTO.languageDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.hs.after.Tomorang.DTO.guideProfileDTO;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class memberServiceImp implements memberService {

    private final memberDAO dao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Set<String> VALID_ROLES = Set.of("GUIDE", "DISCOVERER");

    /** 역할 유효성 검사 */
    private void validateRole(String role) {
        if (role == null || !VALID_ROLES.contains(role.toUpperCase())) {
            throw new IllegalArgumentException(
                "역할은 GUIDE 또는 DISCOVERER만 허용됩니다. (입력값: " + role + ")"
            );
        }
    }

    /** 회원가입 */
    @Override
    @Transactional
    public void insert(memberDTO dto, MultipartFile image) throws IOException {
        validateRole(dto.getRole());

        if (dto.getPw() != null) {
            dto.setPw(passwordEncoder.encode(dto.getPw()));
        }
        if (image != null && !image.isEmpty()) {
            dto.setImage(uploadToS3(image));
        }

        dao.insert(dto);
        saveLanguages(dto);
    }

    /** 회원 정보 수정 */
    @Override
    @Transactional
    public void updateMember(memberDTO dto, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            dto.setImage(uploadToS3(image));
        }
        if (dto.getPw() != null && !dto.getPw().isBlank()) {
            dto.setPw(passwordEncoder.encode(dto.getPw()));
        } else {
            dto.setPw(null);
        }

        dao.updateMember(dto);

        // 언어 수정: 기존 삭제 후 재삽입
        if (dto.getLanguages() != null) {
            dao.deleteLanguages(dto.getId());
            saveLanguages(dto);
        }
    }

    /** 회원 탈퇴 */
    @Override
    @Transactional
    public void deleteMember(String memberId) {
        dao.deleteLanguages(memberId);
        dao.deleteMember(memberId);
    }

    /** 프로필 조회 */
    @Override
    public memberDTO profileSelect(String id) {
        memberDTO dto = dao.selectMember(id);
        if (dto != null) {
            List<languageDTO> rows = dao.selectLanguages(id);
            dto.setLanguages(rows.stream()
                    .map(languageDTO::getLanguage)
                    .collect(Collectors.toList()));
            dto.setLevels(rows.stream()
                    .map(languageDTO::getLevel)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    @Override
    public memberDTO loginSelect(String id) { return dao.loginSelect(id); }

    @Override
    public memberDTO findById(String id) { return dao.findById(id); }

    @Override
    public memberDTO findByNickName(String nickName) { return dao.findByNickName(nickName); }

    /** 역할 전환 (GUIDE ↔ DISCOVERER) */
    @Override
    @Transactional
    public void switchRole(String memberId, String newRole) {
        validateRole(newRole);
        dao.updateRole(memberId, newRole.toUpperCase());
    }

    /** 역할 단건 조회 */
    @Override
    public String getRole(String memberId) {
        return dao.selectRole(memberId);
    }

    /** 인기 안내자 목록 (언어 정보 포함) */
    @Override
    public List<guideProfileDTO> getPopularGuides() {
        List<guideProfileDTO> guides = dao.selectPopularGuides();
        for (guideProfileDTO guide : guides) {
            List<languageDTO> rows = dao.selectLanguages(guide.getId());
            guide.setLanguages(rows.stream().map(languageDTO::getLanguage).collect(Collectors.toList()));
            guide.setLevels(rows.stream().map(languageDTO::getLevel).collect(Collectors.toList()));
        }
        return guides;
    }

    // ─────────────────────────────────────────
    // 내부 메서드
    // ─────────────────────────────────────────

    /** languages[i] + levels[i] 인덱스 맞춰 저장 */
    private void saveLanguages(memberDTO dto) {
        List<LanguageType> languages = dto.getLanguages();
        List<Integer>      levels    = dto.getLevels();
        if (languages == null || languages.isEmpty()) return;

        for (int i = 0; i < languages.size(); i++) {
            int level = (levels != null && levels.size() > i) ? levels.get(i) : 1;
            dao.insertLanguage(dto.getId(), languages.get(i), level);
        }
    }

    /** S3 업로드 공통 */
    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
