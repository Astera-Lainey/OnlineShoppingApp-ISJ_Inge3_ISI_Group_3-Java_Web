package OnlineShopping.service;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.CartItemRepository;
import OnlineShopping.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    public void addToCart(String userEmail, Integer productId, int quantity) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();

        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // Check if the product is already in the cart
        List<CartItem> existingItems = cartItemRepository.findByUser(user);
        for (CartItem item : existingItems) {
            if (item.getProduct().getId().equals(productId)) {
                // Update quantity if product exists in cart
                item.setQuantity(item.getQuantity() + quantity);
                item.setPrice(product.getPrice() * item.getQuantity());
                cartItemRepository.save(item);
                return;
            }
        }

        // Create new cart item
        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice() * quantity);
        cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return cartItemRepository.findByUser(userOpt.get());
    }

    public double calculateCartTotal(String userEmail) {
        List<CartItem> cartItems = getCartItems(userEmail);
        return cartItems.stream().mapToDouble(CartItem::getPrice).sum();
    }
}