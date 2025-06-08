package OnlineShopping.controller;

import OnlineShopping.entity.Cart;
import OnlineShopping.entity.CartItem;
import OnlineShopping.service.CartItemService;
import jakarta.servlet.http.HttpSession;
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
public class CartController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private final CartItemService cartItemService;

    // Display cart page
    @GetMapping("/user/cart")
    public String usersCart(Model model, Authentication authentication) {

        String userEmail = authentication.getName();
        List<CartItem> cartItems = cartItemService.getCartItems(userEmail);
        Double cartTotal = cartItemService.calculateCartTotal(userEmail);
        double cartCount = cartItemService.calculateCartTotal(userEmail);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartCount", cartCount);

        return "/user/cart";
    }

    // Add item to cart (AJAX endpoint)


    @PostMapping("/cart/test")
    @ResponseBody
    public String test() {
        System.out.println("✅ /cart/test was hit");
        return "Test passed";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
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
            CartItem cartItem = cartItemService.addToCart(userEmail, productId, quantity);
            double cartCount = cartItemService.calculateCartTotal(userEmail);

            response.put("success", true);
            response.put("message", "Item added to cart successfully");
            response.put("cartCount", cartCount);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding item to cart: " + e.getMessage());
            logger.error("Error adding item to cart: {}", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

}
