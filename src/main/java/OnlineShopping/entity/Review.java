package OnlineShopping.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.APPROVED;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deletion_reason")
    private String deletionReason;
    
    @Column(name = "deleted_by")
    private String deletedBy; // Admin username who deleted the review
    
    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED,
        DELETED
    }
    
    // Constructors
    public Review() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Review(User user, Product product, Integer rating, String comment) {
        this();
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
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
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public ReviewStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReviewStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public String getDeletionReason() {
        return deletionReason;
    }
    
    public void setDeletionReason(String deletionReason) {
        this.deletionReason = deletionReason;
    }
    
    public String getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isApproved() {
        return status == ReviewStatus.APPROVED;
    }
    
    public boolean isDeleted() {
        return status == ReviewStatus.DELETED;
    }
    
    public boolean isPending() {
        return status == ReviewStatus.PENDING;
    }
    
    public boolean isRejected() {
        return status == ReviewStatus.REJECTED;
    }
}
