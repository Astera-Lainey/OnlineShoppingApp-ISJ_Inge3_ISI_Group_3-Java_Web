package OnlineShopping.service;

import OnlineShopping.entity.WishlistItem;

import java.util.List;

public interface WishlistService {
    /**
     * Add a product to a user's wishlist
     * @param userId the ID of the user
     * @param productId the ID of the product to add
     * @return the created WishlistItem
     */
    WishlistItem addToWishlist(Long userId, Long productId);

    /**
     * Remove a product from a user's wishlist
     * @param userId the ID of the user
     * @param productId the ID of the product to remove
     */
    void removeFromWishlist(Long userId, Long productId);

    /**
     * Get all wishlist items for a user
     * @param userId the ID of the user
     * @return list of WishlistItems
     */
    List<WishlistItem> getUserWishlist(Long userId);

    /**
     * Check if a product is in a user's wishlist
     * @param userId the ID of the user
     * @param productId the ID of the product to check
     * @return true if the product is in the wishlist, false otherwise
     */
    boolean isInWishlist(Long userId, Long productId);
} 