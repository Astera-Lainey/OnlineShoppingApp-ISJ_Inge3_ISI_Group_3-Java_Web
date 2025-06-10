package OnlineShopping.service;

import OnlineShopping.entity.Notification;
import OnlineShopping.entity.Review;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a notification for review deletion
     */
    public Notification createReviewDeletionNotification(Review review, String reason, String deletedBy) {
        String title = "Review Deleted";
        String message = String.format(
            "Your review for product '%s' has been deleted by an administrator.\n\n" +
            "Reason: %s\n" +
            "Deleted by: %s\n" +
            "Date: %s",
            review.getProduct().getName(),
            reason,
            deletedBy,
            LocalDateTime.now().toString()
        );
        
        Notification notification = new Notification(
            review.getUser(),
            title,
            message,
            Notification.NotificationType.REVIEW_DELETED,
            review.getId(),
            "REVIEW"
        );
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Create a notification for review approval
     */
    public Notification createReviewApprovalNotification(Review review) {
        String title = "Review Approved";
        String message = String.format(
            "Your review for product '%s' has been approved and is now visible to other customers.",
            review.getProduct().getName()
        );
        
        Notification notification = new Notification(
            review.getUser(),
            title,
            message,
            Notification.NotificationType.REVIEW_APPROVED,
            review.getId(),
            "REVIEW"
        );
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Create a notification for review rejection
     */
    public Notification createReviewRejectionNotification(Review review, String reason) {
        String title = "Review Rejected";
        String message = String.format(
            "Your review for product '%s' has been rejected.\n\n" +
            "Reason: %s",
            review.getProduct().getName(),
            reason
        );
        
        Notification notification = new Notification(
            review.getUser(),
            title,
            message,
            Notification.NotificationType.REVIEW_REJECTED,
            review.getId(),
            "REVIEW"
        );
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Get all notifications for a user
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, Notification.NotificationStatus.UNREAD);
    }
    
    /**
     * Get recent notifications for a user (with pagination)
     */
    public List<Notification> getRecentNotifications(Long userId, int limit) {
        org.springframework.data.domain.PageRequest pageRequest = 
            org.springframework.data.domain.PageRequest.of(0, limit);
        return notificationRepository.findRecentNotificationsForUser(userId, pageRequest);
    }
    
    /**
     * Count unread notifications for a user
     */
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, Notification.NotificationStatus.UNREAD);
    }
    
    /**
     * Mark a notification as read
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        
        notification.markAsRead();
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }
    
    /**
     * Delete a notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Delete old notifications (cleanup)
     */
    public void deleteOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteOldNotifications(cutoffDate);
    }
    
    /**
     * Get notifications by type for a user
     */
    public List<Notification> getNotificationsByType(Long userId, Notification.NotificationType type) {
        return notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }
    
    /**
     * Get review-related notifications for a user
     */
    public List<Notification> getReviewNotifications(Long userId) {
        List<Notification> reviewNotifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(
            userId, Notification.NotificationType.REVIEW_DELETED);
        reviewNotifications.addAll(notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(
            userId, Notification.NotificationType.REVIEW_APPROVED));
        reviewNotifications.addAll(notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(
            userId, Notification.NotificationType.REVIEW_REJECTED));
        
        // Sort by creation date (most recent first)
        reviewNotifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));
        
        return reviewNotifications;
    }
    
    /**
     * Create a system notification
     */
    public Notification createSystemNotification(User user, String title, String message) {
        Notification notification = new Notification(user, title, message, Notification.NotificationType.SYSTEM_MESSAGE);
        return notificationRepository.save(notification);
    }
    
    /**
     * Get notification by ID
     */
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }
} 