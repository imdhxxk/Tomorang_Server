package kr.hs.after.Tomorang.DAO;

import kr.hs.after.Tomorang.DTO.post.postDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface wishlistDAO {
    void insertWishlist(@Param("memberId") String memberId, @Param("postId") Long postId);
    void deleteWishlist(@Param("memberId") String memberId, @Param("postId") Long postId);
    boolean existsWishlist(@Param("memberId") String memberId, @Param("postId") Long postId);
    List<postDTO> selectWishlists(@Param("memberId") String memberId);
}
