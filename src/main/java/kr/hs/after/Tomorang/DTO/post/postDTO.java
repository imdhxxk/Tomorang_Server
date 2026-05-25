package kr.hs.after.Tomorang.DTO.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "투어 게시물")
public class postDTO {

    @Schema(description = "게시물 ID (자동 생성)", example = "1")
    private Long post_id;

    @Schema(description = "게시물 작성자 ID (가이드)", example = "guide123")
    private String user_id;

    @Schema(description = "투어 제목", example = "서울 야경 투어")
    private String title;

    @Schema(description = "투어 부제목", example = "한강에서 남산까지 야경 명소 탐방")
    private String subtitle;

    @Schema(description = "투어 가격 (원)", example = "50000")
    private int price;

    @Schema(description = "할인율 (%)", example = "20")
    private int discount_rate;

    @Schema(description = "투어 소요 시간", example = "3시간")
    private String duration;

    @Schema(description = "최대 참가 인원", example = "6")
    private int max_participants;

    @Schema(description = "평점 (0.0 ~ 5.0)", example = "4.8")
    private double rating;

    @Schema(description = "리뷰 수", example = "24")
    private int review_count;

    @Schema(description = "좋아요 수", example = "42")
    private int like_count;

    @Schema(description = "도시명", example = "서울")
    private String city_name;

    @Schema(description = "국가명", example = "한국")
    private String country;

    @Schema(description = "위도", example = "37.5665")
    private double lat;

    @Schema(description = "경도", example = "126.9780")
    private double lng;

    @Schema(description = "게시물 생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "게시물 수정 시간")
    private LocalDateTime updatedAt;

    @Schema(description = "할인 적용가 (자동 계산)", example = "40000")
    public int getDiscountedPrice() {
        if (discount_rate <= 0) return price;
        return (int) Math.round(price * (1 - discount_rate / 100.0));
    }

    @Schema(description = "코스 대표 이미지 URL 목록")
    private List<String> images;

    @Schema(description = "본문 콘텐츠 블록 (텍스트/이미지 혼합)")
    private List<contentBlockDTO> contentBlocks;

    @Schema(description = "다국어 태그 목록")
    private List<tagDTO> tags;

    @Schema(description = "예약 가능 일정 목록")
    private List<scheduleDTO> schedules;
}
