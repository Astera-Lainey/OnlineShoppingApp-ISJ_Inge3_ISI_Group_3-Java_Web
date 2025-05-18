package OnlineShopping.entity.services;

import OnlineShopping.entity.Order;
import OnlineShopping.entity.OrderItem;
import OnlineShopping.entity.Transaction;

import java.util.List;

public interface OrderService {
    Order placeOrder(Long userId, Transaction.PaymentMethod paymentMethod);
    Order getOrderById(Long orderId, Long userId);
    List<Order> getUserOrders(Long userId);
    Order cancelOrder(Long orderId, Long userId);
    Order getOrderStatus(Long orderId, Long userId);
    Order trackShipment(Long orderId, Long userId);
    List<OrderItem> getOrderItems(Long orderId, Long userId);
}