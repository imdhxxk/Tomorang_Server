package kr.hs.after.Tomorang.DTO.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class postDTO {
    private Long post_id;
    private String user_id;
    private String title;
    private String subtitle;
    private int price;
    private int discount_rate; //할인률
    private String duration; //투어 시간
    private int max_participants; //최대 인원
    private double rating; //별점
    private int review_count; //리뷰 개수
    private int like_count; //좋아요
    private String city_name; //도시 이름
    private String country; //나라이름
    private double lat; //위도
    private double lng; //경도
    private LocalDateTime createdAt; //게시글 생성 시간
    private LocalDateTime updatedAt; //수정시간

    private List<contentBlockDTO> contentBlocks;
    private List<tagDTO> tags;
    private List<scheduleDTO> schedules;

}
