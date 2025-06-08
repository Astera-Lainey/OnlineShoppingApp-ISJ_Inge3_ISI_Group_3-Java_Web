package OnlineShopping.controller;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.WishList;
import OnlineShopping.service.WishListService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Controller
public class WishListController {
    private final Logger logger = LoggerFactory.getLogger(WishListController.class);

    @Autowired
    private final WishListService wishListService;

    // Display List page
    @GetMapping("/user/wishList")
    public String usersList(Model model, Authentication authentication) {

        String userEmail = authentication.getName();
        List<WishList> wishListItems = wishListService.getWishListItems(userEmail);

        model.addAttribute("wishListItems", wishListItems);
        return "/user/wishlist";
    }

    @PostMapping("/wishlist/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToWishList(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        Map<String, Object> response = new HashMap<>();
        logger.info("🛒 addToCart() was called");

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Authenticated user {}", auth);
            String userEmail = auth.getName();
            logger.info("DEBUG: Authenticated user email = {}", userEmail);

            logger.info("DEBUG: Adding productId = {}, quantity = {}", productId, quantity);

            // Now use userEmail to add to cart
            WishList wishList = wishListService.addToList(userEmail, productId);

            response.put("success", true);
            response.put("message", "Item added to cart successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding item to list: " + e.getMessage());
            logger.error("Error adding item to list: {}", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
