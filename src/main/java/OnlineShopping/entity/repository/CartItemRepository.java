package OnlineShopping.entity.repository;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUser(User user);

    CartItem findByUserAndProduct(User user, Product product);
}