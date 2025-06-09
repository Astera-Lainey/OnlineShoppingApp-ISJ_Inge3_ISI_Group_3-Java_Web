package OnlineShopping.controller;

<<<<<<< HEAD
import OnlineShopping.entity.User;
import OnlineShopping.entity.WishlistItem;
import OnlineShopping.service.UserService;
import OnlineShopping.service.WishlistService;
=======
import OnlineShopping.entity.WishlistItem;
import OnlineShopping.entity.User;
import OnlineShopping.service.WishlistService;
import OnlineShopping.service.UserService;
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
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
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    @Autowired
    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    @GetMapping("/page")
    public String showWishlistPage(Model model) {
<<<<<<< HEAD
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return "redirect:/api/auth/login";
            }

            // Get the email from the authentication principal
            String email = auth.getName();
            User user = userService.findByEmail(email);

            if (user == null) {
                return "redirect:/api/auth/login";
            }

            List<WishlistItem> wishlistItems = wishlistService.getUserWishlist(user.getId());
            model.addAttribute("wishlistItems", wishlistItems);

            System.out.println("Wishlist items found for user " + email + ": " + wishlistItems.size());

            return "user/wishlist";
        } catch (Exception e) {
            System.err.println("Error in showWishlistPage: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/api/auth/login";
        }
=======
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/api/auth/login";
        }

        // Get the email from the authentication principal
        String email = auth.getName();
        Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
        
        if (userOpt.isEmpty()) {
            return "redirect:/api/auth/login";
        }

        User user = userOpt.get();
        List<WishlistItem> wishlistItems = wishlistService.getUserWishlist(user.getId());
        model.addAttribute("wishlistItems", wishlistItems);
        
        return "user/wishlist";
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
    }

    @PostMapping("/add/{productId}")
    @ResponseBody
    public ResponseEntity<?> addToWishlist(@PathVariable Long productId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
<<<<<<< HEAD
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            // Check if already in wishlist
            if (wishlistService.isInWishlist(user.getId(), productId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product is already in wishlist"));
            }

            WishlistItem item = wishlistService.addToWishlist(user.getId(), productId);

=======
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            WishlistItem item = wishlistService.addToWishlist(user.getId(), productId);
            
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product added to wishlist");
            response.put("item", item);
<<<<<<< HEAD

            System.out.println("Successfully added to wishlist: user=" + email + ", productId=" + productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error adding to wishlist: " + e.getMessage());
            e.printStackTrace();
=======
            return ResponseEntity.ok(response);
        } catch (Exception e) {
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
<<<<<<< HEAD
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            wishlistService.removeFromWishlist(user.getId(), productId);

            System.out.println("Successfully removed from wishlist: user=" + email + ", productId=" + productId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Product removed from wishlist"));
        } catch (Exception e) {
            System.err.println("Error removing from wishlist: " + e.getMessage());
            e.printStackTrace();
=======
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            wishlistService.removeFromWishlist(user.getId(), productId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Product removed from wishlist"));
        } catch (Exception e) {
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<?> getWishlistItems() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
<<<<<<< HEAD
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            List<WishlistItem> items = wishlistService.getUserWishlist(user.getId());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            System.err.println("Error getting wishlist items: " + e.getMessage());
            e.printStackTrace();
=======
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            List<WishlistItem> items = wishlistService.getUserWishlist(user.getId());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check/{productId}")
    @ResponseBody
    public ResponseEntity<?> checkWishlistStatus(@PathVariable Long productId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
<<<<<<< HEAD
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            boolean isInWishlist = wishlistService.isInWishlist(user.getId(), productId);
            return ResponseEntity.ok(Map.of("inWishlist", isInWishlist));
        } catch (Exception e) {
            System.err.println("Error checking wishlist status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
=======
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            boolean isInWishlist = wishlistService.isInWishlist(user.getId(), productId);
            return ResponseEntity.ok(Map.of("inWishlist", isInWishlist));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
