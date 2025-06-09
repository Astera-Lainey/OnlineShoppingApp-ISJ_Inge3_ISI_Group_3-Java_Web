package OnlineShopping.entity.repository;

import OnlineShopping.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCartCartId(String cartId);
    Optional<CartItem> findByCartCartIdAndProductId(String cartId, Integer productId);
    void deleteByCartCartIdAndProductId(String cartId, Integer productId);
} 