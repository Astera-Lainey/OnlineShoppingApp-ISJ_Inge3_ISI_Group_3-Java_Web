package OnlineShopping.impl;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.Review;
import OnlineShopping.entity.User;
import OnlineShopping.repository.ItemRepository;
import OnlineShopping.repository.ReviewRepository;
import OnlineShopping.repository.UserRepository;
import OnlineShopping.services.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserReviewServiceImpl implements UserReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public UserReviewServiceImpl(ReviewRepository reviewRepository,
                                 UserRepository userRepository,
                                 ItemRepository itemRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Review addReview(Long userId, Long itemId, String comment, Integer rating) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        // Check if user has already reviewed this item
        List<Review> userReviews = reviewRepository.findByUser(user);
        for (Review existingReview : userReviews) {
            if (existingReview.getItem().getItemId().equals(itemId)) {
                throw new IllegalStateException("You have already reviewed this item");
            }
        }

        Review review = new Review();
        review.setUser(user);
        review.setItem(item);
        review.setReviewComment(comment);
        review.setRating(rating);
        review.setDate(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(Long reviewId, Long userId, String comment, Integer rating) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        // Check if the review belongs to the user
        if (!review.getUser().getUserId()) {
            throw new IllegalArgumentException("You are not authorized to update this review");
        }

        review.setReviewComment(comment);
        review.setRating(rating);
        review.setDate(LocalDateTime.now()); // Update the date

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        // Check if the review belongs to the user
        if (!review.getUser().getUserId()) {
            throw new IllegalArgumentException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<Review> getUserReviews(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return reviewRepository.findByUser(user);
    }

    @Override
    public List<Review> getItemReviews(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        return reviewRepository.findByItemOrderByDateDesc(item);
    }

    @Override
    public Double getItemAverageRating(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        Double averageRating = reviewRepository.findAverageRatingByItem(item);
        return averageRating != null ? averageRating : 0.0;
    }
}