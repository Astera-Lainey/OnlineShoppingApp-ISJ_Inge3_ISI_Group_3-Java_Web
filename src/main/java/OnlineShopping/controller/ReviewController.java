package OnlineShopping.controller;

import OnlineShopping.entity.Review;
import OnlineShopping.entity.User;
import OnlineShopping.service.ReviewService;
import OnlineShopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new review
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReview(@RequestParam Integer productId,
                                        @RequestParam Integer rating,
                                        @RequestParam String comment) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            Review review = reviewService.createReview(user.getId(), productId, rating, comment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review submitted successfully");
            response.put("review", review);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get reviews for a product
     */
    @GetMapping("/product/{productId}")
    @ResponseBody
    public ResponseEntity<?> getProductReviews(@PathVariable Integer productId) {
        try {
            List<Review> reviews = reviewService.getApprovedReviewsForProduct(productId);
            Double avgRating = reviewService.getAverageRatingForProduct(productId);
            long reviewCount = reviewService.getReviewCountForProduct(productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("averageRating", avgRating);
            response.put("reviewCount", reviewCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Check if user has reviewed a product
     */
    @GetMapping("/check/{productId}")
    @ResponseBody
    public ResponseEntity<?> checkUserReview(@PathVariable Integer productId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            boolean hasReviewed = reviewService.hasUserReviewedProduct(user.getId(), productId);
            Optional<Review> userReview = reviewService.getUserReviewForProduct(user.getId(), productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasReviewed", hasReviewed);
            response.put("userReview", userReview.orElse(null));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update user's review
     */
    @PutMapping("/update/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                        @RequestParam Integer rating,
                                        @RequestParam String comment) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            Review review = reviewService.getReviewById(reviewId);
            if (!review.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only update your own reviews"));
            }
            
            Review updatedReview = reviewService.updateReview(reviewId, rating, comment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review updated successfully");
            response.put("review", updatedReview);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Admin: Get all reviews for moderation
     */
    @GetMapping("/admin/moderation")
    public String getReviewsForModeration(Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }
            
            List<Review> pendingReviews = reviewService.getPendingReviews();
            List<Review> allReviewsForModeration = reviewService.getReviewsForModeration();
            
            model.addAttribute("pendingReviews", pendingReviews);
            model.addAttribute("reviewsForModeration", allReviewsForModeration);
            
            return "admin/review-moderation";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while loading reviews for moderation");
            return "admin/review-moderation";
        }
    }
    
    /**
     * Admin: Approve a review
     */
    @PostMapping("/admin/approve/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> approveReview(@PathVariable Long reviewId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            Review review = reviewService.approveReview(reviewId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review approved successfully");
            response.put("review", review);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Admin: Reject a review
     */
    @PostMapping("/admin/reject/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> rejectReview(@PathVariable Long reviewId,
                                        @RequestParam String reason) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            Review review = reviewService.rejectReview(reviewId, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review rejected successfully");
            response.put("review", review);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Admin: Delete a review
     */
    @DeleteMapping("/admin/delete/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
                                        @RequestParam String reason) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String deletedBy = auth.getName();
            Review review = reviewService.deleteReview(reviewId, reason, deletedBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review deleted successfully");
            response.put("review", review);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Admin: Get all deleted reviews
     */
    @GetMapping("/admin/deleted")
    public String getDeletedReviews(Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }
            
            List<Review> deletedReviews = reviewService.getDeletedReviews();
            model.addAttribute("deletedReviews", deletedReviews);
            
            return "admin/deleted-reviews";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while loading deleted reviews");
            return "admin/deleted-reviews";
        }
    }
    
    /**
     * Get a single review by ID
     */
    @GetMapping("/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> getReviewById(@PathVariable Long reviewId) {
        try {
            Review review = reviewService.getReviewById(reviewId);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 