package OnlineShopping;

import OnlineShopping.entity.*;
import OnlineShopping.entity.repository.OrderRepository;
import OnlineShopping.service.OrderService;
import OnlineShopping.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderStatusUpdateTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testUpdateOrderStatus_Success() {
        // Setup test data
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        Order testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setItems(new ArrayList<>());

        // Mock repository calls
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Execute test
        Order result = orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED);

        // Verify results
        assertNotNull(result);
        assertEquals(Order.OrderStatus.CONFIRMED, result.getStatus());
        assertNotNull(result.getUpdatedAt());

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound_ThrowsException() {
        // Mock repository to return empty
        when(orderRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        // Execute test and verify exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus(999L, Order.OrderStatus.CONFIRMED);
        });

        assertEquals("Order not found with ID: 999", exception.getMessage());
    }

    @Test
    void testUpdateOrderStatus_NullOrderId_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus(null, Order.OrderStatus.CONFIRMED);
        });

        assertEquals("Order ID cannot be null", exception.getMessage());
    }

    @Test
    void testUpdateOrderStatus_NullStatus_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus(1L, null);
        });

        assertEquals("Order status cannot be null", exception.getMessage());
    }

    @Test
    void testUpdateOrderStatus_CancelledOrder_RestoresInventory() {
        // Setup test data
        User testUser = new User();
        testUser.setId(1L);

        Product testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setStockQuantity(5);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);

        Order testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(Order.OrderStatus.CONFIRMED);
        testOrder.setItems(new ArrayList<>());
        testOrder.getItems().add(orderItem);

        // Mock repository calls
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Execute test
        Order result = orderService.updateOrderStatus(1L, Order.OrderStatus.CANCELLED);

        // Verify results
        assertNotNull(result);
        assertEquals(Order.OrderStatus.CANCELLED, result.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
        verify(inventoryService).handleOrderCancelled(testOrder);
    }
} 