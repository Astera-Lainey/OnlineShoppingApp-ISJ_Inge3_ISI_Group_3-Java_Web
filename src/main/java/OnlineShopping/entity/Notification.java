package OnlineShopping.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "related_entity_id")
    private Long relatedEntityId; // ID of related entity (e.g., review ID)
    
    @Column(name = "related_entity_type")
    private String relatedEntityType; // Type of related entity (e.g., "REVIEW")
    
    public enum NotificationType {
        REVIEW_DELETED,
        REVIEW_APPROVED,
        REVIEW_REJECTED,
        ORDER_STATUS_CHANGED,
        SYSTEM_MESSAGE
    }
    
    public enum NotificationStatus {
        UNREAD,
        READ
    }
    
    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Notification(User user, String title, String message, NotificationType type) {
        this();
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    
    public Notification(User user, String title, String message, NotificationType type, 
                       Long relatedEntityId, String relatedEntityType) {
        this(user, title, message, type);
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public Long getRelatedEntityId() {
        return relatedEntityId;
    }
    
    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }
    
    public String getRelatedEntityType() {
        return relatedEntityType;
    }
    
    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }
    
    // Helper methods
    public boolean isUnread() {
        return status == NotificationStatus.UNREAD;
    }
    
    public boolean isRead() {
        return status == NotificationStatus.READ;
    }
    
    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }
    
    public boolean isReviewRelated() {
        return type == NotificationType.REVIEW_DELETED || 
               type == NotificationType.REVIEW_APPROVED || 
               type == NotificationType.REVIEW_REJECTED;
    }
} 