package OnlineShopping.entity.repository;

import OnlineShopping.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find all reviews for a specific product
    List<Review> findByProductIdAndStatusOrderByCreatedAtDesc(Integer productId, Review.ReviewStatus status);
    
    // Find all reviews by a specific user
    List<Review> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Review.ReviewStatus status);

    // Find all pending reviews (for admin moderation)
    List<Review> findByStatusOrderByCreatedAtDesc(Review.ReviewStatus status);
    
    // Find all reviews for a product (including deleted ones for admin)
    List<Review> findByProductIdOrderByCreatedAtDesc(Integer productId);
    
    // Find all reviews by a user (including deleted ones for admin)
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Check if user has already reviewed a product
    Optional<Review> findByUserIdAndProductId(Long userId, Integer productId);
    
    // Count reviews by status for a product
    long countByProductIdAndStatus(Integer productId, Review.ReviewStatus status);
    
    // Get average rating for a product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = :status")
    Double getAverageRatingByProductIdAndStatus(@Param("productId") Integer productId, @Param("status") Review.ReviewStatus status);
    
    // Get all reviews that need moderation
    @Query("SELECT r FROM Review r WHERE r.status IN ('PENDING', 'REJECTED') ORDER BY r.createdAt DESC")
    List<Review> findAllPendingAndRejectedReviews();
    
    // Get deleted reviews with deletion reason
    @Query("SELECT r FROM Review r WHERE r.status = 'DELETED' ORDER BY r.deletedAt DESC")
    List<Review> findAllDeletedReviews();
    
    // Get all reviews ordered by creation date (for admin dashboard)
    List<Review> findAllByOrderByCreatedAtDesc();
    
    // Get review by ID with user and product eagerly loaded
    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.product WHERE r.id = :reviewId")
    Optional<Review> findByIdWithUserAndProduct(@Param("reviewId") Long reviewId);
} 