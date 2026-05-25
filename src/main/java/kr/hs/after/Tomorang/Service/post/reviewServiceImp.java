package kr.hs.after.Tomorang.Service.post;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kr.hs.after.Tomorang.DAO.post.postDAO;
import kr.hs.after.Tomorang.DAO.post.reviewDAO;
import kr.hs.after.Tomorang.DTO.post.reviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class reviewServiceImp implements reviewService {

    private final reviewDAO dao;
    private final postDAO   postDao;
    private final AmazonS3  amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    @Transactional
    public void createReview(String memberId, reviewDTO dto, List<MultipartFile> images) throws IOException {
        // postId 검증
        if (dto.getPostId() == null) {
            throw new IllegalArgumentException("postId는 필수입니다.");
        }
        if (postDao.selectPostById(dto.getPostId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 게시물입니다. (postId: " + dto.getPostId() + ")");
        }

        // 별점 검증
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");
        }

        // 리뷰 저장
        dao.insertReview(memberId, dto);

        // 이미지 S3 업로드 + 저장
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String url = uploadToS3(file);
                    dao.insertReviewImage(dto.getId(), url);
                }
            }
        }

        // 게시물 평점·리뷰수 자동 업데이트
        dao.updatePostRating(dto.getPostId());
    }

    @Override
    public List<reviewDTO> getReviews(Long postId, String memberId) {
        List<reviewDTO> reviews = dao.selectReviewsByPostId(postId, memberId);
        for (reviewDTO review : reviews) {
            review.setImages(dao.selectReviewImages(review.getId()));
        }
        return reviews;
    }

    @Override
    @Transactional
    public void likeReview(Long reviewId, String memberId) {
        dao.insertReviewLike(reviewId, memberId);
    }

    @Override
    @Transactional
    public void unlikeReview(Long reviewId, String memberId) {
        dao.deleteReviewLike(reviewId, memberId);
    }

    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentType(file.getContentType());
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), meta));
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
