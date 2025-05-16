package OnlineShopping.services;

import OnlineShopping.entity.Review;
import java.util.List;

public interface AdminReviewService {
    List<Review> getAllReviews();
    Review getReviewById(Long reviewId);
    void deleteReview(Long reviewId);
    List<Review> getReviewsByItemId(Long itemId);
    List<Review> getReviewsByUserId(Long userId);
}
