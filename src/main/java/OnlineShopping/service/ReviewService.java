package OnlineShopping.service;

import OnlineShopping.entity.Product;
import OnlineShopping.entity.Review;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.ReviewRepository;
import OnlineShopping.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Create a new review
     */
    public Review createReview(Long userId, Integer productId, Integer rating, String comment) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        // Check if user has already reviewed this product
        Optional<Review> existingReview = reviewRepository.findByUserIdAndProductId(userId, productId);
        if (existingReview.isPresent()) {
            throw new IllegalStateException("You have already reviewed this product");
        }
        
        // Get user and product
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        // Create review
        Review review = new Review(user, product, rating, comment);
        review.setStatus(Review.ReviewStatus.APPROVED); // Auto-approve for now, can be changed to PENDING for moderation
        
        return reviewRepository.save(review);
    }
    
    /**
     * Get all approved reviews for a product
     */
    public List<Review> getApprovedReviewsForProduct(Integer productId) {
        return reviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(productId, Review.ReviewStatus.APPROVED);
    }
    
    /**
     * Get all reviews for a product (admin only)
     */
    public List<Review> getAllReviewsForProduct(Integer productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
    
    /**
     * Get all reviews by a user
     */
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, Review.ReviewStatus.APPROVED);
    }
    
    /**
     * Get all reviews by a user (admin only)
     */
    public List<Review> getAllReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get all pending reviews for moderation
     */
    public List<Review> getPendingReviews() {
        return reviewRepository.findByStatusOrderByCreatedAtDesc(Review.ReviewStatus.PENDING);
    }
    
    /**
     * Get all reviews that need moderation
     */
    public List<Review> getReviewsForModeration() {
        return reviewRepository.findAllPendingAndRejectedReviews();
    }
    
    /**
     * Approve a review
     */
    public Review approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.setStatus(Review.ReviewStatus.APPROVED);
        review.setUpdatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        // Send notification to user
        notificationService.createReviewApprovalNotification(savedReview);
        
        return savedReview;
    }
    
    /**
     * Reject a review
     */
    public Review rejectReview(Long reviewId, String reason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.setStatus(Review.ReviewStatus.REJECTED);
        review.setDeletionReason(reason);
        review.setUpdatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        // Send notification to user
        notificationService.createReviewRejectionNotification(savedReview, reason);
        
        return savedReview;
    }
    
    /**
     * Delete a review (admin only)
     */
    public Review deleteReview(Long reviewId, String reason, String deletedBy) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.setStatus(Review.ReviewStatus.DELETED);
        review.setDeletionReason(reason);
        review.setDeletedBy(deletedBy);
        review.setDeletedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        // Send notification to user about review deletion
        notificationService.createReviewDeletionNotification(savedReview, reason, deletedBy);
        
        return savedReview;
    }
    
    /**
     * Get average rating for a product
     */
    public Double getAverageRatingForProduct(Integer productId) {
        Double avgRating = reviewRepository.getAverageRatingByProductIdAndStatus(productId, Review.ReviewStatus.APPROVED);
        return avgRating != null ? avgRating : 0.0;
    }
    
    /**
     * Get review count for a product
     */
    public long getReviewCountForProduct(Integer productId) {
        return reviewRepository.countByProductIdAndStatus(productId, Review.ReviewStatus.APPROVED);
    }
    
    /**
     * Check if user has reviewed a product
     */
    public boolean hasUserReviewedProduct(Long userId, Integer productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }
    
    /**
     * Get user's review for a product
     */
    public Optional<Review> getUserReviewForProduct(Long userId, Integer productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId);
    }
    
    /**
     * Update a review
     */
    public Review updateReview(Long reviewId, Integer rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        review.setRating(rating);
        review.setComment(comment);
        review.setUpdatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }
    
    /**
     * Get all deleted reviews (admin only)
     */
    public List<Review> getDeletedReviews() {
        return reviewRepository.findAllDeletedReviews();
    }
    
    /**
     * Get review by ID
     */
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findByIdWithUserAndProduct(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
    }
    
    /**
     * Get all reviews (admin only)
     */
    public List<Review> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }
} 