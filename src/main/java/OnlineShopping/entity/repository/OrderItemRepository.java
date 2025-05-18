package OnlineShopping.entity.repository;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.Order;
import OnlineShopping.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByItem(Item item);
}