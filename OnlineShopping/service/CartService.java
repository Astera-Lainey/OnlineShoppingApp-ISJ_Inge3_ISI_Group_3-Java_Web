package OnlineShopping.service;

import OnlineShopping.entity.CartItem;

import java.util.List;

public interface CartService {
    /**
     * Add a product to a user's cart
     * @param userId the ID of the user
     * @param productId the ID of the product to add
     * @param quantity the quantity to add
     * @return the created CartItem
     */
    CartItem addToCart(Long userId, Integer productId, int quantity);

    /**
     * Remove a product from a user's cart
     * @param userId the ID of the user
     * @param productId the ID of the product to remove
     */
    void removeFromCart(Long userId, Integer productId);

    /**
     * Update the quantity of a cart item
     * @param userId the ID of the user
     * @param productId the ID of the product to update
     * @param quantity the new quantity
     * @return the updated CartItem
     */
    CartItem updateCartItemQuantity(Long userId, Integer productId, int quantity);

    /**
     * Get all cart items for a user
     * @param userId the ID of the user
     * @return list of CartItems
     */
    List<CartItem> getCartItems(Long userId);
} 