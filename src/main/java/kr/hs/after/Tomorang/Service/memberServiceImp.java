package kr.hs.after.Tomorang.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hs.after.Tomorang.DAO.memberDAO;
import kr.hs.after.Tomorang.DTO.LanguageDTO;
import kr.hs.after.Tomorang.DTO.memberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // 생성자 주입을 @Autowired 대신 편하게 해주는 어노테이션
public class memberServiceImp implements memberService{

    private final memberDAO dao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    public void insert(memberDTO dto) {
        // 1. 비밀번호 암호화
        dto.setPw(passwordEncoder.encode(dto.getPw()));

        try {
            // 2. language 리스트를 JSON 문자열로 압축해서 langLv에 넣기
            if (dto.getLanguage() != null) {
                String json = objectMapper.writeValueAsString(dto.getLanguage());
                dto.setLangLv(json); // 압축 완료!
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 에러", e);
        }

        // 4. 이제 DB에 저장 (dto 안에 암호화 비번과 JSON 문자열이 다 들어있음)
        dao.insert(dto);
    }

    @Override
    public memberDTO profileSelect(String id){
        memberDTO dto = dao.profile(id);
        if (dto != null && dto.getLangLv() != null) {
            try {
                // langLv(글자)를 다시 List<LanguageDTO>로 풀어서 language에 넣기
                List<LanguageDTO> list = objectMapper.readValue(
                        dto.getLangLv(),
                        new TypeReference<List<LanguageDTO>>() {}
                );
                dto.setLanguage(list); // 압축 해제 완료!
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return dto;
    }

    @Override
    public memberDTO loginSelect(String id) {
        return dao.loginSelect(id);
    }

    @Override
    public memberDTO findById(String id) { return dao.findById(id); }

    @Override
    public memberDTO findByNickName(String nickName) {
        return dao.findByNickName(nickName);
    }


}
