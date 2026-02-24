package kr.hs.after.Tomorang.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hs.after.Tomorang.DAO.memberDAO;
import kr.hs.after.Tomorang.DTO.LanguageDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // lombok.Value와 헷갈리지 마세요!
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        if (image != null && !image.isEmpty()) {
            // 1. 중복 방지 파일명 생성
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

            // 2. 파일 메타데이터 설정 (중요!)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());

            // 3. S3로 바로 업로드 (로컬 저장소 거치지 않음)
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            // 4. S3 URL 추출 후 DTO에 저장
            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
            dto.setImage(fileUrl);
        }

        // 비밀번호 암호화 및 DB 저장
        if(dto.getPw() != null) dto.setPw(passwordEncoder.encode(dto.getPw()));
        dao.insert(dto);
    }

    @Override
    public memberDTO profileSelect(String id) {
        memberDTO dto = dao.profileSelect(id);
        if (dto != null && dto.getLangLv() != null) {
            try {
                List<LanguageDTO> list = objectMapper.readValue(
                        dto.getLangLv(),
                        new TypeReference<List<LanguageDTO>>() {}
                );
                dto.setLanguage(list);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
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