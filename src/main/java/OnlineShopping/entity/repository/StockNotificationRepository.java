package OnlineShopping.entity.repository;

import OnlineShopping.entity.StockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    List<StockNotification> findByIsReadOrderByCreatedAtDesc(boolean isRead);
    List<StockNotification> findByProductIdAndTypeOrderByCreatedAtDesc(Long productId, StockNotification.NotificationType type);
    long countByIsRead(boolean isRead);
} 