package OnlineShopping.service.impl;

import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import OnlineShopping.entity.WishlistItem;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.repository.WishlistRepository;
import OnlineShopping.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               UserRepository userRepository,
                               ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public WishlistItem addToWishlist(Long userId, Long productId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if product exists - Convert Long to Integer for Product ID
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if item already exists in wishlist
        Optional<WishlistItem> existingItem = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (existingItem.isPresent()) {
            throw new RuntimeException("Product is already in wishlist");
        }

        // Create new wishlist item
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setUser(user);
        wishlistItem.setProduct(product);

        WishlistItem savedItem = wishlistRepository.save(wishlistItem);
        System.out.println("Successfully saved wishlist item: " + savedItem.getId());

        return savedItem;
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        WishlistItem item = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in wishlist"));

        wishlistRepository.delete(item);
        System.out.println("Successfully removed wishlist item for user: " + userId + ", product: " + productId);
    }

    @Override
    public List<WishlistItem> getUserWishlist(Long userId) {
        List<WishlistItem> items = wishlistRepository.findByUserIdOrderByAddedAtDesc(userId);
        System.out.println("Found " + items.size() + " wishlist items for user: " + userId);
        return items;
    }

    @Override
    public boolean isInWishlist(Long userId, Long productId) {
        boolean exists = wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent();
        System.out.println("Product " + productId + " is in wishlist for user " + userId + ": " + exists);
        return exists;
    }
}