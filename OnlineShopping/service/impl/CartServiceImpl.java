package OnlineShopping.service.impl;

import OnlineShopping.entity.Cart;
import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.CartItemRepository;
import OnlineShopping.entity.repository.CartRepository;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          UserRepository userRepository,
                          ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CartItem addToCart(Long userId, Integer productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Get or create cart for user
        Cart cart = cartRepository.findByCustomerId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCartId(UUID.randomUUID().toString());
                    newCart.setCustomer(user);
                    return cartRepository.save(newCart);
                });

        // Check if product already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartCartIdAndProductId(cart.getCartId(), productId);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setPrice(product.getPrice());
            return cartItemRepository.save(item);
        }

        // Create new cart item
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());
        cartItem.setCart(cart);
        return cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Integer productId) {
        Cart cart = cartRepository.findByCustomerId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartItemRepository.deleteByCartCartIdAndProductId(cart.getCartId(), productId);
    }

    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Integer productId, int quantity) {
        Cart cart = cartRepository.findByCustomerId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem cartItem = cartItemRepository.findByCartCartIdAndProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> getCartItems(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByCustomerId(userId);
        if (cartOpt.isEmpty()) {
            // User doesn't have a cart yet, return empty list
            return new ArrayList<>();
        }
        Cart cart = cartOpt.get();
        return cartItemRepository.findByCartCartId(cart.getCartId());
    }
} 