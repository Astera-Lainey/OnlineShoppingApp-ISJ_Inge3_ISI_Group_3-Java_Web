package OnlineShopping.entity.services;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.ShoppingCart;

import java.util.List;

public interface CartService {
    CartItem addToCart(Long userId, Long itemId, Integer quantity);
    void removeFromCart(Long userId, Long itemId);
    void updateCartItemQuantity(Long userId, Long itemId, Integer quantity);
    void clearCart(Long userId);
    ShoppingCart getUserCart(Long userId);
    List<CartItem> getUserCartItems(Long userId);
    Double getCartTotal(Long userId);
}