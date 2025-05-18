package OnlineShopping.entity.repository;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Item;
import OnlineShopping.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
   public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find all cart items for a specific cart
    List<CartItem> findByCart(ShoppingCart cart);

    // Find all cart items containing a specific item
    List<CartItem> findByItem(Item item);

    // Find a specific cart item by cart and item
    Optional<CartItem> findByCartAndItem(ShoppingCart cart, Item item);

    // Count items in a specific cart
    Long countByCart(ShoppingCart cart);

    // Delete all items from a cart
    void deleteByCart(ShoppingCart cart);

    // Delete a specific item from all carts
    void deleteByItem(Item item);
}

