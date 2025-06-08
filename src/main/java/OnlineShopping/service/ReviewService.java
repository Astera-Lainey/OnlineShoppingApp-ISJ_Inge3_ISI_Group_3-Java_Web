package OnlineShopping.service;


import OnlineShopping.entity.Review;
import OnlineShopping.entity.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findByProductId(Integer productId) {
        return reviewRepository.findByProductId(productId);
    }

    public void saveReview(Review review) {
        reviewRepository.save(review);
}
}
