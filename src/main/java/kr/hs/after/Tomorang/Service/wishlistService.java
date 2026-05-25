package kr.hs.after.Tomorang.Service;

import kr.hs.after.Tomorang.DTO.post.postDTO;

import java.util.List;

public interface wishlistService {
    void addWishlist(String memberId, Long postId);
    void removeWishlist(String memberId, Long postId);
    boolean isWishlisted(String memberId, Long postId);
    List<postDTO> getWishlists(String memberId);
}
