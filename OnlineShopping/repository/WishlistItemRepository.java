package OnlineShopping.repository;

import OnlineShopping.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    
    List<WishlistItem> findByUserId(Long userId);
    
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);
    
    @Query("SELECT w FROM WishlistItem w JOIN FETCH w.product WHERE w.user.id = :userId")
    List<WishlistItem> findByUserIdWithProduct(Long userId);
    
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
} 