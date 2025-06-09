package OnlineShopping.controller;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import OnlineShopping.service.CartService;
import OnlineShopping.service.ProductService;
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
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @PostMapping("/add/{productId}")
    @ResponseBody
    public ResponseEntity<?> addToCart(@PathVariable Integer productId, @RequestParam(defaultValue = "1") int quantity) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            Product product = (Product) productService.getProductById(productId);
            
            if (product == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product not found"));
            }

            if (product.getStockQuantity() < quantity) {
                return ResponseEntity.badRequest().body(Map.of("error", "Not enough stock available"));
            }

            CartItem cartItem = cartService.addToCart(user.getId(), productId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product added to cart");
            response.put("item", cartItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(@PathVariable Integer productId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            cartService.removeFromCart(user.getId(), productId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Product removed from cart"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update/{productId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Integer productId, @RequestParam int quantity) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            Product product = (Product) productService.getProductById(productId);
            
            if (product == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product not found"));
            }

            if (product.getStockQuantity() < quantity) {
                return ResponseEntity.badRequest().body(Map.of("error", "Not enough stock available"));
            }

            CartItem cartItem = cartService.updateCartItemQuantity(user.getId(), productId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cart updated successfully");
            response.put("item", cartItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<?> getCartItems() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/view")
    public String viewCart(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return "redirect:/login";
            }

            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return "redirect:/login";
            }

            User user = userOpt.get();
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            double subtotal = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            double shipping = subtotal > 0 ? 45.0 : 0.0; // Example shipping cost
            double total = subtotal + shipping;

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shipping", shipping);
            model.addAttribute("total", total);
            
            return "user/cart";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/cart";
        }
    }
} 