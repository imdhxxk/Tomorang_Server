package kr.hs.after.Tomorang.Service.post;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kr.hs.after.Tomorang.DAO.post.postDAO;
import kr.hs.after.Tomorang.DTO.post.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value; // lombok.Value와 헷갈리지 마세요!

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class postServiceImp implements postService {

    private final postDAO dao;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional // 하나라도 실패하면 전체 취소(롤백)
    public void createPost(postDTO dto, List<MultipartFile> courseImages, List<MultipartFile> contentImages) throws IOException {

        // 1. 메인 게시글 저장 (성공 시 dto.postId에 ID가 자동으로 채워짐)
        dao.postInsert(dto);
        Long postId = dto.getPost_id();

        // 2. 상단 코스 이미지들 처리
        if (courseImages != null) {
            for (MultipartFile file : courseImages) {
                String url = uploadToS3(file);
                dao.insertPostImage(postId, url);
            }
        }

        // 3. 본문 블록 처리 (글 + 본문용 사진)
        // 3. 본문 블록 처리 (글 + 본문용 사진)
        int contentImgIdx = 0;
        if (dto.getContentBlocks() != null) {
            for (contentBlockDTO block : dto.getContentBlocks()) {
                if ("image".equals(block.getType())) {
                    // 이미지 파일이 리스트에 남아있는지 확인하는 조건문 추가
                    if (contentImages != null && contentImgIdx < contentImages.size()) {
                        String url = uploadToS3(contentImages.get(contentImgIdx++));
                        block.setValue(url);
                    } else {
                        // 이미지가 부족하면 해당 블록은 무시하거나 빈 값을 넣습니다.
                        continue;
                    }
                }
                dao.insertPostContent(postId, block);
            }
        }

        // 4. 태그 처리 (다국어)
        if (dto.getTags() != null) {
            for (tagDTO tag : dto.getTags()) {
                dao.insertPostTag(postId, tag);
            }
        }

        // 5. 스케줄 및 타임슬롯 처리
        if (dto.getSchedules() != null) {
            for (scheduleDTO schedule : dto.getSchedules()) {
                // schedule 저장용 맵 (ID를 돌려받기 위해)
                Map<String, Object> scheduleMap = new HashMap<>();
                scheduleMap.put("postId", postId);
                scheduleMap.put("date", schedule.getDate());

                dao.insertPostSchedule(scheduleMap);
                Long scheduleId = Long.valueOf(String.valueOf(scheduleMap.get("scheduleId")));
                for (timeSlotDTO slot : schedule.getTimeSlots()) {
                    dao.insertTimeSlot(scheduleId, slot);
                }
            }
        }
    }

    @Override
    public List<postDTO> getPostList(String keyword, String city, String country, String userId) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        List<postDTO> posts = dao.selectPosts(kw, city, country, userId);
        for (postDTO post : posts) {
            post.setImages(dao.selectPostImages(post.getPost_id()));
            post.setContentBlocks(dao.selectPostContents(post.getPost_id()));
        }
        return posts;
    }

    @Override
    public postDTO getPostDetail(Long postId) {
        postDTO post = dao.selectPostById(postId);
        if (post == null) return null;

        post.setImages(dao.selectPostImages(postId));
        post.setContentBlocks(dao.selectPostContents(postId));
        post.setTags(dao.selectPostTags(postId));

        List<scheduleDTO> schedules = dao.selectSchedulesByPostId(postId);
        for (scheduleDTO schedule : schedules) {
            schedule.setTimeSlots(dao.selectTimeSlotsByScheduleId(schedule.getScheduleId()));
        }
        post.setSchedules(schedules);

        return post;
    }

    // S3 업로드 공통 메서드
    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}