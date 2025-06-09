package OnlineShopping;

import OnlineShopping.entity.*;
import OnlineShopping.entity.repository.OrderRepository;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.service.OrderService;
import OnlineShopping.service.CartService;
import OnlineShopping.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrder_Success() {
        // Setup test data
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        Product testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(29.99);
        testProduct.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setPrice(29.99);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        Order expectedOrder = new Order();
        expectedOrder.setId(1L);
        expectedOrder.setUser(testUser);

        // Mock repository calls
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);

        // Execute test
        Order result = orderService.createOrder(
            testUser,
            cartItems,
            "123 Test St",
            "Test City",
            "Test State",
            "12345",
            "123-456-7890",
            "Test notes"
        );

        // Verify results
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUser, result.getUser());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_NullUser_ThrowsException() {
        List<CartItem> cartItems = new ArrayList<>();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(
                null,
                cartItems,
                "123 Test St",
                "Test City",
                "Test State",
                "12345",
                "123-456-7890",
                "Test notes"
            );
        });

        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void testCreateOrder_EmptyCartItems_ThrowsException() {
        User testUser = new User();
        testUser.setId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(
                testUser,
                new ArrayList<>(),
                "123 Test St",
                "Test City",
                "Test State",
                "12345",
                "123-456-7890",
                "Test notes"
            );
        });

        assertEquals("Cart items cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateOrder_EmptyShippingAddress_ThrowsException() {
        User testUser = new User();
        testUser.setId(1L);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(
                testUser,
                cartItems,
                "",
                "Test City",
                "Test State",
                "12345",
                "123-456-7890",
                "Test notes"
            );
        });

        assertEquals("Shipping address is required", exception.getMessage());
    }
} 