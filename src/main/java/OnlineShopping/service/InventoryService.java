package OnlineShopping.service;

import OnlineShopping.entity.*;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.StockNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockNotificationRepository notificationRepository;

    private static final double LOW_STOCK_THRESHOLD = 0.25; // 25% of initial stock

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateStock(Integer productId, int newQuantity) {
        try {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Stock quantity cannot be negative");
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

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
        } catch (Exception e) {
            System.err.println("Error updating stock: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void handleOrderPlaced(Order order) {
        try {
            if (order == null) {
                throw new IllegalArgumentException("Order cannot be null");
            }
            if (order.getItems() == null || order.getItems().isEmpty()) {
                throw new IllegalArgumentException("Order items cannot be empty");
            }

            System.out.println("=== INVENTORY SERVICE: Handling order placed ===");
            System.out.println("Order ID: " + order.getId());
            System.out.println("Items count: " + order.getItems().size());

            for (OrderItem item : order.getItems()) {
                if (item.getProduct() == null) {
                    throw new IllegalArgumentException("Product is null for order item");
                }
                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Invalid quantity for order item: " + item.getQuantity());
                }

                Product product = item.getProduct();
                int currentStock = product.getStockQuantity();
                int newQuantity = currentStock - item.getQuantity();
                
                if (newQuantity < 0) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() + 
                        " (Available: " + currentStock + ", Requested: " + item.getQuantity() + ")");
                }

                product.setStockQuantity(newQuantity);
                productRepository.save(product);

                System.out.println("Updated stock for " + product.getName() + ": " + currentStock + " -> " + newQuantity);

                // Create notification for order placed
                createStockNotification(product, StockNotification.NotificationType.ORDER_PLACED,
                        String.format("Order #%d placed for %s: %d units", order.getId(), product.getName(), item.getQuantity()));

                // Check for low stock
                checkLowStock(product);
            }

            System.out.println("=== INVENTORY SERVICE: Order placed handling completed ===");
        } catch (Exception e) {
            System.err.println("=== INVENTORY SERVICE: Order placed handling failed ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void handleOrderCancelled(Order order) {
        try {
            if (order == null) {
                throw new IllegalArgumentException("Order cannot be null");
            }
            if (order.getItems() == null || order.getItems().isEmpty()) {
                throw new IllegalArgumentException("Order items cannot be empty");
            }

            System.out.println("=== INVENTORY SERVICE: Handling order cancelled ===");
            System.out.println("Order ID: " + order.getId());
            System.out.println("Items count: " + order.getItems().size());

            for (OrderItem item : order.getItems()) {
                if (item.getProduct() == null) {
                    throw new IllegalArgumentException("Product is null for order item");
                }
                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Invalid quantity for order item: " + item.getQuantity());
                }

                Product product = item.getProduct();
                int currentStock = product.getStockQuantity();
                int newQuantity = currentStock + item.getQuantity();
                
                product.setStockQuantity(newQuantity);
                productRepository.save(product);

                System.out.println("Restored stock for " + product.getName() + ": " + currentStock + " -> " + newQuantity);

                // Create notification for order cancelled
                createStockNotification(product, StockNotification.NotificationType.ORDER_CANCELLED,
                        String.format("Order #%d cancelled for %s: %d units restored", order.getId(), product.getName(), item.getQuantity()));

                // Check for low stock
                checkLowStock(product);
            }

            System.out.println("=== INVENTORY SERVICE: Order cancelled handling completed ===");
        } catch (Exception e) {
            System.err.println("=== INVENTORY SERVICE: Order cancelled handling failed ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void checkLowStock(Product product) {
        try {
            int currentStock = product.getStockQuantity();
            int initialStock = product.getInitialStockQuantity();

            if (currentStock <= 0) {
                createStockNotification(product, StockNotification.NotificationType.OUT_OF_STOCK,
                        String.format("%s is out of stock!", product.getName()));
            } else if (currentStock <= (initialStock * LOW_STOCK_THRESHOLD)) {
                createStockNotification(product, StockNotification.NotificationType.LOW_STOCK,
                        String.format("Low stock alert: %s has only %d units remaining", product.getName(), currentStock));
            }
        } catch (Exception e) {
            System.err.println("Error checking low stock for product " + product.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createStockNotification(Product product, StockNotification.NotificationType type, String message) {
        try {
            StockNotification notification = StockNotification.builder()
                    .product(product)
                    .type(type)
                    .message(message)
                    .build();
            notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("Error creating stock notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    public List<StockNotification> getUnreadNotifications() {
        return notificationRepository.findByIsReadOrderByCreatedAtDesc(false);
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount() {
        return notificationRepository.countByIsRead(false);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void markNotificationAsRead(Long notificationId) {
        try {
            notificationRepository.findById(notificationId).ifPresent(notification -> {
                notification.setRead(true);
                notificationRepository.save(notification);
            });
        } catch (Exception e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void markAllNotificationsAsRead() {
        try {
            notificationRepository.findByIsReadOrderByCreatedAtDesc(false).forEach(notification -> {
                notification.setRead(true);
                notificationRepository.save(notification);
            });
        } catch (Exception e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public long getLowStockCount() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStockQuantity() <= (product.getInitialStockQuantity() * LOW_STOCK_THRESHOLD))
                .count();
    }
} 