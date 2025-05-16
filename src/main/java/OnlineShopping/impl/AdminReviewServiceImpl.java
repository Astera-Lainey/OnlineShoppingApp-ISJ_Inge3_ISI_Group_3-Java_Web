package OnlineShopping.impl;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.Review;
import OnlineShopping.entity.User;
import OnlineShopping.repository.ItemRepository;
import OnlineShopping.repository.ReviewRepository;
import OnlineShopping.repository.UserRepository;
import OnlineShopping.services.AdminReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminReviewServiceImpl implements AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public AdminReviewServiceImpl(ReviewRepository reviewRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
        } else {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }
    }

    @Override
    public List<Review> getReviewsByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        return reviewRepository.findByItem(item);
    }

    @Override
    public List<Review> getReviewsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return reviewRepository.findByUser(user);
    }
}
