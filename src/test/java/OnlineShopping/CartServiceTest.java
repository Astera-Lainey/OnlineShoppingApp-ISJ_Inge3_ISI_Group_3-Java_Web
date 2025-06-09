package OnlineShopping;

import OnlineShopping.entity.Cart;
import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.CartItemRepository;
import OnlineShopping.entity.repository.CartRepository;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(29.99);
        testProduct.setStockQuantity(10);

        // Setup test cart
        testCart = new Cart();
        testCart.setCartId("test-cart-id");
        testCart.setCustomer(testUser);
    }

    @Test
    void testAddToCart_NewItem_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartCartIdAndProductId("test-cart-id", 1)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            item.setCartItemId(1);
            return item;
        });
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        CartItem result = cartService.addToCart(1L, 1, 2);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartItemId());
        assertEquals(testProduct, result.getProduct());
        assertEquals(2, result.getQuantity());
        assertEquals(29.99, result.getPrice());
        assertEquals(59.98, result.getTotalPrice());
        assertEquals(testCart, result.getCart());

        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(testCart);
    }

    @Test
    void testAddToCart_ExistingItem_Success() {
        // Arrange
        CartItem existingItem = new CartItem();
        existingItem.setCartItemId(1);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(1);
        existingItem.setPrice(29.99);
        existingItem.setCart(testCart);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartCartIdAndProductId("test-cart-id", 1)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            return item;
        });

        // Act
        CartItem result = cartService.addToCart(1L, 1, 2);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getQuantity()); // 1 + 2
        assertEquals(29.99, result.getPrice());
        assertEquals(89.97, result.getTotalPrice()); // 29.99 * 3

        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void testAddToCart_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(999L, 1, 1);
        });

        assertEquals("User not found with ID: 999", exception.getMessage());
    }

    @Test
    void testAddToCart_ProductNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(1L, 999, 1);
        });

        assertEquals("Product not found with ID: 999", exception.getMessage());
    }

    @Test
    void testAddToCart_InsufficientStock_ThrowsException() {
        // Arrange
        testProduct.setStockQuantity(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(1L, 1, 2);
        });

        assertEquals("Not enough stock available. Available: 1, Requested: 2", exception.getMessage());
    }

    @Test
    void testAddToCart_InvalidQuantity_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(1L, 1, 0);
        });

        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void testAddToCart_NullUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(null, 1, 1);
        });

        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    void testAddToCart_NullProductId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(1L, null, 1);
        });

        assertEquals("Product ID cannot be null", exception.getMessage());
    }
} 