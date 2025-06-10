package OnlineShopping.entity.repository;

import OnlineShopping.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find all notifications for a user
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find unread notifications for a user
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Notification.NotificationStatus status);
    
    // Count unread notifications for a user
    long countByUserIdAndStatus(Long userId, Notification.NotificationStatus status);
    
    // Find notifications by type for a user
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, Notification.NotificationType type);
    
    // Find notifications related to a specific entity
    List<Notification> findByRelatedEntityIdAndRelatedEntityTypeOrderByCreatedAtDesc(Long relatedEntityId, String relatedEntityType);
    
    // Find all notifications of a specific type
    List<Notification> findByTypeOrderByCreatedAtDesc(Notification.NotificationType type);
    
    // Find notifications for a user with pagination
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsForUser(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
    
    // Mark all notifications as read for a user
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.status = 'UNREAD'")
    void markAllAsReadForUser(@Param("userId") Long userId);
    
    // Delete old notifications (older than specified days)
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
} 