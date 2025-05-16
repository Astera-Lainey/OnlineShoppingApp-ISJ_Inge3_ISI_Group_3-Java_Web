package OnlineShopping.impl;

import OnlineShopping.entity.*;
import OnlineShopping.repository.*;
import OnlineShopping.services.CartService;
import OnlineShopping.services.OrderService;
import OnlineShopping.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final StockRepository stockRepository;
    private final TransactionService transactionService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            CartService cartService,
                            StockRepository stockRepository,
                            TransactionService transactionService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.stockRepository = stockRepository;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional
    public Order placeOrder(Long userId, Transaction.PaymentMethod paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Get cart items
        List<CartItem> cartItems = cartService.getUserCartItems(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart");
        }

        // Check stock availability
        checkStockAvailability(cartItems);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatus.PENDING);
        order.setOrderTotal(cartService.getCartTotal(userId));
        order.setItemsOrdered(cartItems.size());

        Order savedOrder = orderRepository.save(order);

        // Create order items and reduce stock
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setItem(cartItem.getItem());
            orderItem.setQuantity(cartItem.getQuantity());

            orderItems.add(orderItemRepository.save(orderItem));

            // Reduce stock
            reduceItemStock(cartItem.getItem().getItemId(), cartItem.getQuantity());
        }

        // Clear cart after successful order
        cartService.clearCart(userId);

        // Process payment
        transactionService.processPayment(savedOrder.getOrderId(), paymentMethod, savedOrder.getOrderTotal());

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Security check - ensure order belongs to user
        if (!order.getUser().getUserId()) {
            throw new SecurityException("You are not authorized to view this order");
        }

        return order;
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return orderRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);

        // Check if order can be cancelled
        if (order.getOrderStatus() == Order.OrderStatus.SHIPPED ||
                order.getOrderStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order that has been shipped or delivered");
        }

        // Update order status
        order.setOrderStatus(Order.OrderStatus.CANCELLED);

        // Return items to stock
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        for (OrderItem orderItem : orderItems) {
            increaseItemStock(orderItem.getItem().getItemId(), orderItem.getQuantity());
        }

        // Update transaction status if exists
        if (order.getTransaction() != null) {
            transactionService.updateTransactionStatus(
                    order.getTransaction().getTransactionId(),
                    Transaction.TransactionStatus.REFUNDED
            );
        }

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderStatus(Long orderId, Long userId) {
        return getOrderById(orderId, userId);
    }

    @Override
    public Order trackShipment(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);

        // In a real implementation, you might call a shipping API here
        // to get the latest tracking information

        return order;
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);
        return orderItemRepository.findByOrder(order);
    }

    private void checkStockAvailability(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Item item = cartItem.getItem();
            Optional<Stock> stockOpt = stockRepository.findByItem(item);

            if (stockOpt.isEmpty() || stockOpt.get().getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for item: " + item.getItemName());
            }
        }
    }

    private void reduceItemStock(Long itemId, Integer quantity) {
        Item item = new Item();
        item.setItemId(itemId);

        Optional<Stock> stockOpt = stockRepository.findByItem(item);

        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            stock.setQuantity(stock.getQuantity() - quantity);
            stockRepository.save(stock);
        }
    }

    private void increaseItemStock(Long itemId, Integer quantity) {
        Item item = new Item();
        item.setItemId(itemId);

        Optional<Stock> stockOpt = stockRepository.findByItem(item);

        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            stock.setQuantity(stock.getQuantity() + quantity);
            stockRepository.save(stock);
        }
    }
}
