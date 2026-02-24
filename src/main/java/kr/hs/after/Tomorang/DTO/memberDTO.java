package kr.hs.after.Tomorang.DTO;

import lombok.Data;

import java.util.List;

@Data
public class memberDTO {
    private String id;
    private String pw;
    private String role;
    private String email;
    private String interest;
    private String image;
    private String nickName;
    private String oneWord;
    private String langLv;
    private List<LanguageDTO> language;
}

