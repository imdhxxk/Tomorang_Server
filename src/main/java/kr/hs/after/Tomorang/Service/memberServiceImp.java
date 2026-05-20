package kr.hs.after.Tomorang.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hs.after.Tomorang.DAO.memberDAO;
import kr.hs.after.Tomorang.DTO.languageDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // lombok.Value와 헷갈리지 마세요!
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class memberServiceImp implements memberService {

    private final memberDAO dao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AmazonS3 amazonS3; // S3Config에서 만든 빈 주입

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public void insert(memberDTO dto, MultipartFile image) throws IOException {

        // 1️⃣ 회원 저장
        if(dto.getPw() != null) {
            dto.setPw(passwordEncoder.encode(dto.getPw()));
        }

        dao.insert(dto);

        // 2️⃣ 언어 저장
        if (dto.getLanguages() != null && !dto.getLanguages().isEmpty()) {


            for (languageDTO lang : dto.getLanguages()) {
                dao.insertLanguage(
                        dto.getId(),
                        lang.getLanguage(),
                        lang.getLevel()
                );
            }
        }
    }

    public memberDTO profileSelect(String id) {

        // 1️⃣ 회원 정보
        memberDTO dto = dao.selectMember(id);

        if (dto != null) {
            // 2️⃣ 언어 리스트 조회
            List<languageDTO> languages = dao.selectLanguages(id).getLanguages();
            dto.setLanguages(languages);
        }

        return dto;
    }

    @Override
    public memberDTO loginSelect(String id) { return dao.loginSelect(id); }

    @Override
    public memberDTO findById(String id) { return dao.findById(id); }

    @Override
    public memberDTO findByNickName(String nickName) { return dao.findByNickName(nickName); }
}