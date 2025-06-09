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
            // Validate authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            // Validate input parameters
            if (productId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product ID cannot be null"));
            }
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quantity must be greater than 0"));
            }

            // Get user
            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            
            // Validate product exists
            Product product = productService.getProductById(productId);
            if (product == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product not found"));
            }

            // Check stock availability
            if (product.getStockQuantity() < quantity) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                    "Not enough stock available. Available: " + product.getStockQuantity() + ", Requested: " + quantity));
            }

            // Add to cart
            CartItem cartItem = cartService.addToCart(user.getId(), productId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product added to cart successfully");
            response.put("item", cartItem);
            response.put("productName", product.getName());
            response.put("quantity", quantity);
            response.put("price", product.getPrice());
            response.put("totalPrice", cartItem.getTotalPrice());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error in addToCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred while adding item to cart"));
        }
    }

    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(@PathVariable Integer productId) {
        try {
            // Validate authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            // Validate input parameters
            if (productId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product ID cannot be null"));
            }

            // Get user
            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            
            // Remove from cart
            cartService.removeFromCart(user.getId(), productId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Product removed from cart successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error in removeFromCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred while removing item from cart"));
        }
    }

    @PutMapping("/update/{productId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Integer productId, @RequestParam int quantity) {
        try {
            // Validate authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            // Validate input parameters
            if (productId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product ID cannot be null"));
            }
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quantity must be greater than 0"));
            }

            // Get user
            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            
            // Validate product exists
            Product product = productService.getProductById(productId);
            if (product == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product not found"));
            }

            // Check stock availability
            if (product.getStockQuantity() < quantity) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                    "Not enough stock available. Available: " + product.getStockQuantity() + ", Requested: " + quantity));
            }

            // Update cart item
            CartItem cartItem = cartService.updateCartItemQuantity(user.getId(), productId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cart updated successfully");
            response.put("item", cartItem);
            response.put("productName", product.getName());
            response.put("quantity", quantity);
            response.put("price", product.getPrice());
            response.put("totalPrice", cartItem.getTotalPrice());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error in updateCartItemQuantity: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred while updating cart"));
        }
    }

    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<?> getCartItems() {
        try {
            // Validate authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            // Get user
            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            
            // Get cart items
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            
            // Calculate totals
            double subtotal = cartItems.stream()
                    .mapToDouble(item -> item.getTotalPrice())
                    .sum();
            double shipping = subtotal > 0 ? 45.0 : 0.0; // Example shipping cost
            double total = subtotal + shipping;

            Map<String, Object> response = new HashMap<>();
            response.put("items", cartItems);
            response.put("subtotal", subtotal);
            response.put("shipping", shipping);
            response.put("total", total);
            response.put("itemCount", cartItems.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Unexpected error in getCartItems: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred while retrieving cart items"));
        }
    }

    @GetMapping("/view")
    public String viewCart(Model model) {
        try {
            // Validate authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return "redirect:/login";
            }

            // Get user
            String email = auth.getName();
            Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));
            
            if (userOpt.isEmpty()) {
                return "redirect:/login";
            }

            User user = userOpt.get();
            
            // Get cart items
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            
            // Calculate totals
            double subtotal = cartItems.stream()
                    .mapToDouble(item -> item.getTotalPrice())
                    .sum();
            double shipping = subtotal > 0 ? 45.0 : 0.0; // Example shipping cost
            double total = subtotal + shipping;

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shipping", shipping);
            model.addAttribute("total", total);
            model.addAttribute("itemCount", cartItems.size());
            
            return "user/cart";
        } catch (Exception e) {
            System.err.println("Error in viewCart: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while loading the cart. Please try again later.");
            return "user/cart";
        }
    }
} 