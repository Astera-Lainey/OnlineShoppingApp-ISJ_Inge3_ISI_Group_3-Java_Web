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
import org.springframework.transaction.annotation.Propagation;

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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CartItem addToCart(Long userId, Integer productId, int quantity) {
        try {
            // Validate input parameters
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            // Find user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Find product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

            // Check stock availability
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
            }

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
                int newQuantity = item.getQuantity() + quantity;
                
                // Check if new total quantity exceeds stock
                if (product.getStockQuantity() < newQuantity) {
                    throw new RuntimeException("Not enough stock available for total quantity. Available: " + product.getStockQuantity() + ", Total requested: " + newQuantity);
                }
                
                item.setQuantity(newQuantity);
                item.setPrice(product.getPrice());
                item.setTotalPrice(product.getPrice() * newQuantity);
                return cartItemRepository.save(item);
            }

            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
            cartItem.setTotalPrice(product.getPrice() * quantity);
            cartItem.setCart(cart);
            
            CartItem savedItem = cartItemRepository.save(cartItem);
            
            // Update cart's items list
            cart.addItem(savedItem);
            cartRepository.save(cart);
            
            return savedItem;
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to trigger rollback
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeFromCart(Long userId, Integer productId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }

            Cart cart = cartRepository.findByCustomerId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + userId));
            
            cartItemRepository.deleteByCartCartIdAndProductId(cart.getCartId(), productId);
        } catch (Exception e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CartItem updateCartItemQuantity(Long userId, Integer productId, int quantity) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            Cart cart = cartRepository.findByCustomerId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + userId));
            
            CartItem cartItem = cartItemRepository.findByCartCartIdAndProductId(cart.getCartId(), productId)
                    .orElseThrow(() -> new RuntimeException("Cart item not found for product ID: " + productId));

            // Check stock availability
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException("Not enough stock available. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
            }

            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(product.getPrice() * quantity);
            return cartItemRepository.save(cartItem);
        } catch (Exception e) {
            System.err.println("Error updating cart item quantity: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        try {
            if (userId == null) {
                return new ArrayList<>();
            }

            Optional<Cart> cartOpt = cartRepository.findByCustomerId(userId);
            if (cartOpt.isEmpty()) {
                // User doesn't have a cart yet, return empty list
                return new ArrayList<>();
            }
            Cart cart = cartOpt.get();
            return cartItemRepository.findByCartCartId(cart.getCartId());
        } catch (Exception e) {
            System.err.println("Error getting cart items: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
} 