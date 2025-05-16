package OnlineShopping.services;

import OnlineShopping.entity.Review;

import java.util.List;

public interface UserReviewService {
    Review addReview(Long userId, Long itemId, String comment, Integer rating);
    Review updateReview(Long reviewId, Long userId, String comment, Integer rating);
    void deleteReview(Long reviewId, Long userId);
    List<Review> getUserReviews(Long userId);
    List<Review> getItemReviews(Long itemId);
    Double getItemAverageRating(Long itemId);
}