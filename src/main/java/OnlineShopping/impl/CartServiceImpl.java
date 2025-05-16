package OnlineShopping.impl;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Item;
import OnlineShopping.entity.ShoppingCart;
import OnlineShopping.entity.User;
import OnlineShopping.repository.CartItemRepository;
import OnlineShopping.repository.ItemRepository;
import OnlineShopping.repository.ShoppingCartRepository;
import OnlineShopping.repository.UserRepository;
import OnlineShopping.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository,
                           ShoppingCartRepository cartRepository,
                           UserRepository userRepository,
                           ItemRepository itemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public CartItem addToCart(Long userId, Long itemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        // Get or create shopping cart for user
        ShoppingCart cart = getOrCreateCart(user);

        // Check if item already exists in cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndItem(cart, item);

        if (existingCartItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setItem(item);
            cartItem.setQuantity(quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        ShoppingCart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found for user"));

        Optional<CartItem> cartItem = cartItemRepository.findByCartAndItem(cart, item);

        cartItem.ifPresent(cartItemRepository::delete);
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Long userId, Long itemId, Integer quantity) {
        if (quantity <= 0) {
            // If quantity is 0 or negative, remove the item
            removeFromCart(userId, itemId);
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        ShoppingCart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found for user"));

        CartItem cartItem = cartItemRepository.findByCartAndItem(cart, item)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Optional<ShoppingCart> cartOpt = cartRepository.findByUser(user);

        if (cartOpt.isPresent()) {
            ShoppingCart cart = cartOpt.get();
            cartItemRepository.deleteByCart(cart);
        }
    }

    @Override
    public ShoppingCart getUserCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));
    }

    @Override
    public List<CartItem> getUserCartItems(Long userId) {
        ShoppingCart cart = getUserCart(userId);
        return cartItemRepository.findByCart(cart);
    }

    @Override
    public Double getCartTotal(Long userId) {
        List<CartItem> cartItems = getUserCartItems(userId);

        return cartItems.stream()
                .mapToDouble(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();
    }

    private ShoppingCart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));
    }

    private ShoppingCart createCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}