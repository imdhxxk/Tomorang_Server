package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DAO.wishlistDAO;
import kr.hs.after.Tomorang.DAO.post.postDAO;
import kr.hs.after.Tomorang.DTO.post.postDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class wishlistServiceImp implements wishlistService {

    private final wishlistDAO dao;
    private final postDAO     postDAO;

    @Override
    @Transactional
    public void addWishlist(String memberId, Long postId) {
        dao.insertWishlist(memberId, postId);
    }

    @Override
    @Transactional
    public void removeWishlist(String memberId, Long postId) {
        dao.deleteWishlist(memberId, postId);
    }

    @Override
    public boolean isWishlisted(String memberId, Long postId) {
        return dao.existsWishlist(memberId, postId);
    }

    @Override
    public List<postDTO> getWishlists(String memberId) {
        List<postDTO> posts = dao.selectWishlists(memberId);
        // 각 게시물의 대표 이미지 첨부
        for (postDTO post : posts) {
            post.setImages(postDAO.selectPostImages(post.getPost_id()));
        }
        return posts;
    }
}
