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

    @Autowired
    private InventoryService inventoryService;

    @Transactional
    public Order createOrder(User user, List<CartItem> cartItems, String shippingAddress,
                           String shippingCity, String shippingState, String shippingZipCode,
                           String shippingPhone, String notes) {
        
        System.out.println("=== ORDER SERVICE: Creating order ===");
        System.out.println("User: " + user.getUsername() + " (ID: " + user.getId() + ")");
        System.out.println("Cart items: " + cartItems.size());
        
        // Validate input parameters
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart items cannot be empty");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new RuntimeException("Shipping address is required");
        }
        if (shippingCity == null || shippingCity.trim().isEmpty()) {
            throw new RuntimeException("Shipping city is required");
        }
        if (shippingState == null || shippingState.trim().isEmpty()) {
            throw new RuntimeException("Shipping state is required");
        }
        if (shippingZipCode == null || shippingZipCode.trim().isEmpty()) {
            throw new RuntimeException("Shipping ZIP code is required");
        }
        if (shippingPhone == null || shippingPhone.trim().isEmpty()) {
            throw new RuntimeException("Shipping phone is required");
        }

        // Validate stock availability before creating order
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new RuntimeException("Product information is missing for cart item");
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock available for product: " + product.getName() + 
                    " (Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity() + ")");
            }
        }

        System.out.println("Validation passed, creating order...");

        // Create new order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress.trim())
                .shippingCity(shippingCity.trim())
                .shippingState(shippingState.trim())
                .shippingZipCode(shippingZipCode.trim())
                .shippingPhone(shippingPhone.trim())
                .notes(notes != null ? notes.trim() : "")
                .status(Order.OrderStatus.PENDING)
                .build();

        // Calculate totals and create order items
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
            System.out.println("Order item: " + cartItem.getProduct().getName() + 
                " - Qty: " + cartItem.getQuantity() + 
                " - Price: " + cartItem.getPrice() + 
                " - Total: " + orderItem.getTotalPrice());
        }

        // Set order totals
        double shippingCost = subtotal > 0 ? 45.0 : 0.0; // Example shipping cost
        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotal(subtotal + shippingCost);

        System.out.println("Order totals - Subtotal: " + subtotal + 
            ", Shipping: " + shippingCost + 
            ", Total: " + order.getTotal());

        // Save order
        System.out.println("Saving order to database...");
        Order savedOrder = orderRepository.save(order);
        System.out.println("Order saved with ID: " + savedOrder.getId());

        // Update inventory (reduce stock)
        System.out.println("Updating inventory...");
        inventoryService.handleOrderPlaced(savedOrder);

        // Clear cart after successful order creation
        System.out.println("Clearing cart...");
        for (CartItem cartItem : cartItems) {
            cartService.removeFromCart(user.getId(), cartItem.getProduct().getId());
        }

        System.out.println("=== ORDER SERVICE: Order creation completed ===");
        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
        return orderRepository.findByUser(user);
    }

    public Page<Order> getUserOrders(User user, Pageable pageable) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
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
        if (orderId == null) {
            throw new RuntimeException("Order ID cannot be null");
        }
        if (newStatus == null) {
            throw new RuntimeException("Order status cannot be null");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Update status
        order.setStatus(newStatus);
        
        // If order is cancelled, restore inventory
        if (newStatus == Order.OrderStatus.CANCELLED) {
            inventoryService.handleOrderCancelled(order);
        }
        
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        if (orderId == null) {
            throw new RuntimeException("Order ID cannot be null");
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    public long getTotalOrderCount() {
        return orderRepository.count();
    }

    public long getOrderCountByStatus(Order.OrderStatus status) {
        if (status == null) {
            return orderRepository.count();
        }
        return orderRepository.countByStatus(status);
    }
} 