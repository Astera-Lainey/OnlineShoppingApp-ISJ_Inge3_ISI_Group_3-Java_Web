package OnlineShopping.repository;

import OnlineShopping.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT w FROM WishlistItem w WHERE w.user.id = :userId AND w.product.id = :productId")
    Optional<WishlistItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT w FROM WishlistItem w JOIN FETCH w.product p LEFT JOIN FETCH p.images WHERE w.user.id = :userId ORDER BY w.addedAt DESC")
    List<WishlistItem> findByUserIdOrderByAddedAtDesc(@Param("userId") Long userId);

    /**
     * Count wishlist items for a user
     */
    @Query("SELECT COUNT(w) FROM WishlistItem w WHERE w.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

} 