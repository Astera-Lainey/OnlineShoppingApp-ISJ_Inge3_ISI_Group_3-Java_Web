package OnlineShopping.service;

import OnlineShopping.entity.*;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.StockNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockNotificationRepository notificationRepository;

    private static final double LOW_STOCK_THRESHOLD = 0.25; // 25% of initial stock

    @Transactional
    public void updateStock(Integer productId, int newQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        int oldQuantity = product.getStockQuantity();
        
        // If this is the first time setting stock, update initial stock quantity
        if (product.getInitialStockQuantity() == 0) {
            product.setInitialStockQuantity(newQuantity);
        }
        
        product.setStockQuantity(newQuantity);
        productRepository.save(product);

        // Create notification for stock update
        createStockNotification(product, StockNotification.NotificationType.STOCK_UPDATED,
                String.format("Stock updated for %s: %d -> %d", product.getName(), oldQuantity, newQuantity));

        // Check for low stock
        checkLowStock(product);
    }

    @Transactional
    public void handleOrderPlaced(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getStockQuantity() - item.getQuantity();
            product.setStockQuantity(newQuantity);
            productRepository.save(product);

            // Create notification for order placed
            createStockNotification(product, StockNotification.NotificationType.ORDER_PLACED,
                    String.format("Order #%d placed for %s: %d units", order.getId(), product.getName(), item.getQuantity()));

            // Check for low stock
            checkLowStock(product);
        }
    }

    @Transactional
    public void handleOrderCancelled(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getStockQuantity() + item.getQuantity();
            product.setStockQuantity(newQuantity);
            productRepository.save(product);

            // Create notification for order cancelled
            createStockNotification(product, StockNotification.NotificationType.ORDER_CANCELLED,
                    String.format("Order #%d cancelled for %s: %d units restored", order.getId(), product.getName(), item.getQuantity()));

            // Check for low stock
            checkLowStock(product);
        }
    }

    private void checkLowStock(Product product) {
        int currentStock = product.getStockQuantity();
        int initialStock = product.getInitialStockQuantity(); // You'll need to add this field to Product entity

        if (currentStock <= 0) {
            createStockNotification(product, StockNotification.NotificationType.OUT_OF_STOCK,
                    String.format("%s is out of stock!", product.getName()));
        } else if (currentStock <= (initialStock * LOW_STOCK_THRESHOLD)) {
            createStockNotification(product, StockNotification.NotificationType.LOW_STOCK,
                    String.format("Low stock alert: %s has only %d units remaining", product.getName(), currentStock));
        }
    }

    private void createStockNotification(Product product, StockNotification.NotificationType type, String message) {
        StockNotification notification = StockNotification.builder()
                .product(product)
                .type(type)
                .message(message)
                .build();
        notificationRepository.save(notification);
    }

    public List<StockNotification> getUnreadNotifications() {
        return notificationRepository.findByIsReadOrderByCreatedAtDesc(false);
    }

    public long getUnreadNotificationCount() {
        return notificationRepository.countByIsRead(false);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllNotificationsAsRead() {
        notificationRepository.findByIsReadOrderByCreatedAtDesc(false).forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public long getLowStockCount() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStockQuantity() <= (product.getInitialStockQuantity() * LOW_STOCK_THRESHOLD))
                .count();
    }
} 