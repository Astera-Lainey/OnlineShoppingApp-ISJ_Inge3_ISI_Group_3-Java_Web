package OnlineShopping.service;

import OnlineShopping.entity.*;
import OnlineShopping.entity.repository.OrderRepository;
import OnlineShopping.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Order createOrder(User user, List<CartItem> cartItems, String shippingAddress,
                           String shippingCity, String shippingState, String shippingZipCode,
                           String shippingPhone, String notes) {
        
        try {
            System.out.println("=== ORDER SERVICE: Creating order ===");
            System.out.println("User: " + user.getUsername() + " (ID: " + user.getId() + ")");
            System.out.println("Cart items: " + cartItems.size());
            
            // Validate input parameters
            validateOrderInput(user, cartItems, shippingAddress, shippingCity, shippingState, shippingZipCode, shippingPhone);
            
            // Validate stock availability before creating order
            validateStockAvailability(cartItems);
            
            System.out.println("Validation passed, creating order...");

            // Create new order
            Order order = new Order();
            order.setUser(user);
            order.setShippingAddress(shippingAddress.trim());
            order.setShippingCity(shippingCity.trim());
            order.setShippingState(shippingState.trim());
            order.setShippingZipCode(shippingZipCode.trim());
            order.setShippingPhone(shippingPhone.trim());
            order.setNotes(notes != null ? notes.trim() : "");
            order.setStatus(Order.OrderStatus.PENDING);
            order.setCreatedAt(LocalDateTime.now());
            order.setItems(new ArrayList<>());

            // Calculate totals and create order items
            double subtotal = 0;
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = createOrderItem(order, cartItem);
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

            // Update inventory (reduce stock) - handle safely
            System.out.println("Updating inventory...");
            try {
                inventoryService.handleOrderPlaced(savedOrder);
                System.out.println("Inventory updated successfully");
            } catch (Exception e) {
                System.err.println("Warning: Failed to update inventory: " + e.getMessage());
                e.printStackTrace();
                // Don't fail the order creation if inventory update fails
            }

            // Clear cart after successful order creation
            System.out.println("Clearing cart...");
            clearCartItems(user.getId(), cartItems);

            System.out.println("=== ORDER SERVICE: Order creation completed ===");
            return savedOrder;
            
        } catch (Exception e) {
            System.err.println("=== ORDER SERVICE: Order creation failed ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to trigger rollback
        }
    }

    private void validateOrderInput(User user, List<CartItem> cartItems, String shippingAddress,
                                  String shippingCity, String shippingState, String shippingZipCode, String shippingPhone) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart items cannot be empty");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required");
        }
        if (shippingCity == null || shippingCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping city is required");
        }
        if (shippingState == null || shippingState.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping state is required");
        }
        if (shippingZipCode == null || shippingZipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping ZIP code is required");
        }
        if (shippingPhone == null || shippingPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping phone is required");
        }
    }

    private void validateStockAvailability(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new IllegalArgumentException("Product information is missing for cart item");
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock available for product: " + product.getName() + 
                    " (Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity() + ")");
            }
        }
    }

    private OrderItem createOrderItem(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getPrice());
        orderItem.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());
        return orderItem;
    }

    private void clearCartItems(Long userId, List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            try {
                cartService.removeFromCart(userId, cartItem.getProduct().getId());
                System.out.println("Removed item from cart: " + cartItem.getProduct().getName());
            } catch (Exception e) {
                System.err.println("Warning: Failed to remove item from cart: " + e.getMessage());
                e.printStackTrace();
                // Don't fail the order creation if cart clearing fails
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return orderRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        // Find user by ID first, then get their orders
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return orderRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Page<Order> getUserOrders(User user, Pageable pageable) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return orderRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        if (status == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        if (status == null) {
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findByStatus(status, pageable);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        try {
            System.out.println("=== ORDER SERVICE: Updating order status ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("New Status: " + newStatus);
            
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            if (newStatus == null) {
                throw new IllegalArgumentException("Order status cannot be null");
            }

            System.out.println("Finding order in database...");
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
            
            System.out.println("Order found: " + order.getId());
            System.out.println("Current status: " + order.getStatus());
            
            // Update status
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            
            System.out.println("Status updated to: " + order.getStatus());
            
            // If order is cancelled, restore inventory
            if (newStatus == Order.OrderStatus.CANCELLED) {
                System.out.println("Order is being cancelled, restoring inventory...");
                try {
                    inventoryService.handleOrderCancelled(order);
                    System.out.println("Inventory restored successfully");
                } catch (Exception e) {
                    System.err.println("Warning: Failed to restore inventory for cancelled order: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Saving updated order...");
            Order savedOrder = orderRepository.save(order);
            System.out.println("Order saved successfully");
            System.out.println("=== ORDER SERVICE: Order status update completed ===");
            
            return savedOrder;
        } catch (Exception e) {
            System.err.println("=== ORDER SERVICE: Order status update failed ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    }

    @Transactional(readOnly = true)
    public long getTotalOrderCount() {
        return orderRepository.count();
    }

    @Transactional(readOnly = true)
    public long getOrderCountByStatus(Order.OrderStatus status) {
        if (status == null) {
            return orderRepository.count();
        }
        return orderRepository.countByStatus(status);
    }
} 