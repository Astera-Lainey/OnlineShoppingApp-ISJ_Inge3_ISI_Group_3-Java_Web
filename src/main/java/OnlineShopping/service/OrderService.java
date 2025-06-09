package OnlineShopping.service;

import OnlineShopping.entity.*;
import OnlineShopping.entity.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Transactional
    public Order createOrder(User user, List<CartItem> cartItems, String shippingAddress,
                           String shippingCity, String shippingState, String shippingZipCode,
                           String shippingPhone, String notes) {
        // Create new order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .shippingCity(shippingCity)
                .shippingState(shippingState)
                .shippingZipCode(shippingZipCode)
                .shippingPhone(shippingPhone)
                .notes(notes)
                .status(Order.OrderStatus.PENDING)
                .build();

        // Calculate totals
        double subtotal = 0;
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
            order.addItem(orderItem);
            subtotal += orderItem.getTotalPrice();
        }

        // Set order totals
        double shippingCost = subtotal > 0 ? 45.0 : 0.0; // Example shipping cost
        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotal(subtotal + shippingCost);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order creation
        for (CartItem cartItem : cartItems) {
            cartService.removeFromCart(user.getId(), cartItem.getProduct().getId());
        }

        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public Page<Order> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        if (status == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findByStatus(status);
    }

    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        if (status == null) {
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
} 