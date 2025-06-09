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
            System.out.println("=== CHECKOUT PROCESS STARTED ===");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                System.out.println("Authentication failed");
                return "redirect:/api/auth/login";
            }

            String userEmail = auth.getName();
            System.out.println("User email: " + userEmail);
            
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(userEmail));
            
            if (userOpt.isEmpty()) {
                System.out.println("User not found for email: " + userEmail);
                return "redirect:/api/auth/login";
            }

            User user = userOpt.get();
            System.out.println("User found: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            // Verify that the form data matches the authenticated user
            if (!user.getEmail().equals(email) || !user.getUsername().equals(username)) {
                System.out.println("User information mismatch");
                redirectAttributes.addFlashAttribute("error", "Invalid user information");
                return "redirect:/user/checkout";
            }
            
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            System.out.println("Cart items found: " + cartItems.size());
            
            if (cartItems.isEmpty()) {
                System.out.println("Cart is empty");
                redirectAttributes.addFlashAttribute("error", "Your cart is empty");
                return "redirect:/user/cart";
            }

            // Log cart items for debugging
            for (CartItem item : cartItems) {
                System.out.println("Cart item: " + item.getProduct().getName() + 
                    " - Qty: " + item.getQuantity() + 
                    " - Price: " + item.getPrice() + 
                    " - Total: " + item.getTotalPrice());
            }

            System.out.println("Creating order...");
            
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

            System.out.println("Order created successfully with ID: " + order.getId());

            // Enhanced success message with order details
            String successMessage = String.format(
                "🎉 Order #%d placed successfully!%n" +
                "📦 Total Items: %d%n" +
                "💰 Total Amount: $%.2f%n" +
                "📮 Shipping to: %s, %s, %s%n" +
                "📞 Contact: %s%n" +
                "⏰ Order Date: %s%n" +
                "📋 Status: %s%n" +
                "Thank you for your purchase! You will receive an email confirmation shortly.",
                order.getId(),
                order.getItems().size(),
                order.getTotal(),
                shippingAddress,
                shippingCity,
                shippingState,
                shippingPhone,
                order.getCreatedAt().toString(),
                order.getStatus()
            );
            
            redirectAttributes.addFlashAttribute("success", successMessage);
            redirectAttributes.addFlashAttribute("orderId", order.getId());
            redirectAttributes.addFlashAttribute("orderTotal", order.getTotal());
            
            System.out.println("=== CHECKOUT PROCESS COMPLETED SUCCESSFULLY ===");
            return "redirect:/user/main";
            
        } catch (Exception e) {
            System.err.println("=== CHECKOUT PROCESS FAILED ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing your order: " + e.getMessage());
            return "redirect:/user/checkout";
        }
    }
} 