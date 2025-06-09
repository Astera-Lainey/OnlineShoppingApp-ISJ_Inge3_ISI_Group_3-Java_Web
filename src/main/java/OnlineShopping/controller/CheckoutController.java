package OnlineShopping.controller;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Order;
import OnlineShopping.entity.User;
import OnlineShopping.service.CartService;
import OnlineShopping.service.OrderService;
import OnlineShopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/process")
    public String processCheckout(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String shippingAddress,
            @RequestParam String shippingCity,
            @RequestParam String shippingState,
            @RequestParam String shippingZipCode,
            @RequestParam String shippingPhone,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return "redirect:/api/auth/login";
            }

            String userEmail = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(userEmail));
            
            if (userOpt.isEmpty()) {
                return "redirect:/api/auth/login";
            }

            User user = userOpt.get();
            
            // Verify that the form data matches the authenticated user
            if (!user.getEmail().equals(email) || !user.getUsername().equals(username)) {
                redirectAttributes.addFlashAttribute("error", "Invalid user information");
                return "redirect:/user/checkout";
            }
            
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            
            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Your cart is empty");
                return "redirect:/user/cart";
            }

            // Create the order
            Order order = orderService.createOrder(
                user,
                cartItems,
                shippingAddress,
                shippingCity,
                shippingState,
                shippingZipCode,
                shippingPhone,
                notes
            );

            redirectAttributes.addFlashAttribute("success", "Your order #" + order.getId() + " has been placed successfully!");
            return "redirect:/user/main";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing your order. Please try again.");
            return "redirect:/user/checkout";
        }
    }
} 