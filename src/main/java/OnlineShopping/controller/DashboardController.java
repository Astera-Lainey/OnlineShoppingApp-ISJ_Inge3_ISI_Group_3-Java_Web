package OnlineShopping.controller;

import OnlineShopping.dto.ImageDTO;
import OnlineShopping.dto.ProductDTO;
import OnlineShopping.entity.*;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.service.CartService;
import OnlineShopping.service.ProductImageService;
import OnlineShopping.service.ProductService;
import OnlineShopping.service.OrderService;
import OnlineShopping.service.InventoryService;
import OnlineShopping.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ReviewService reviewService;

    //admin getMappings
    @GetMapping("admin/adminDashboard")
    public String adminDashboard(
            @RequestParam(required = false) Order.OrderStatus status,
            Model model,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }

        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser.isEmpty()) {
            return "redirect:/api/auth/login";
        }

        // Add orders to the model based on status filter
        List<Order> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            // Default to pending orders if no status specified
            orders = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);
        }
        
        System.out.println("=== ADMIN DASHBOARD: Loading orders ===");
        System.out.println("Status filter: " + (status != null ? status : "PENDING"));
        System.out.println("Orders found: " + orders.size());
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getId() + 
                ", User: " + order.getUser().getUsername() + 
                ", Status: " + order.getStatus() + 
                ", Total: " + order.getTotal());
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status != null ? status : Order.OrderStatus.PENDING);

        // Add existing model attributes
        List<Product> allProducts = productService.getAllProducts();
        List<ProductImage> images = productImageService.getAllImages();
        List<ImageDTO> imagedto = new ArrayList<>();
        for (ProductImage image : images) {
            imagedto.add(image.toDTO());
        }
        model.addAttribute("products", allProducts);
        model.addAttribute("productform", new ProductDTO());
        model.addAttribute("cats", Category.values());
        model.addAttribute("images", imagedto);

        // Add admin-specific statistics
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalOrders", orderService.getOrdersByStatus(null).size()); // Get all orders for total count
        model.addAttribute("pendingOrders", orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING)
                .count());

        // Add inventory management attributes
        model.addAttribute("lowStockCount", inventoryService.getLowStockCount());
        model.addAttribute("unreadNotificationCount", inventoryService.getUnreadNotificationCount());

        // Add review management attributes
        List<Review> allReviews = reviewService.getAllReviews();
        List<Review> pendingReviews = reviewService.getPendingReviews();
        List<Review> reviewsForModeration = reviewService.getReviewsForModeration();
        
        model.addAttribute("allReviews", allReviews);
        model.addAttribute("pendingReviews", pendingReviews);
        model.addAttribute("reviewsForModeration", reviewsForModeration);
        model.addAttribute("totalReviews", allReviews.size());
        model.addAttribute("pendingReviewCount", pendingReviews.size());
        model.addAttribute("moderationReviewCount", reviewsForModeration.size());

        return "admin/adminDashboard";
    }


    //user getMappings
    @GetMapping("user/main")
    public String userDashboard(Authentication authentication, Model model) {
        try {
            // Authentication check
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }

            Optional<User> currentUserOpt = userRepository.findByEmail(authentication.getName());
            if (currentUserOpt.isEmpty()) {
                return "redirect:/api/auth/login";
            }

            // Initialize model attributes
            model.addAttribute("currentUser", currentUserOpt.get());
            model.addAttribute("categories", Category.values());

            // Get products with their images
            List<Product> products = productService.getAllProducts();
            if (products != null) {
                // Initialize empty images list for products that might not have images
                products.forEach(product -> {
                    if (product.getImages() == null) {
                        product.setImages(new ArrayList<>());
                    }
                });
                model.addAttribute("products", products);
            } else {
                model.addAttribute("products", new ArrayList<>());
            }

            // Get all product images
            List<ProductImage> images = productImageService.getAllImages();
            List<ImageDTO> imageDtos = images != null ? 
                images.stream()
                    .map(ProductImage::toDTO)
                    .collect(Collectors.toList()) : 
                new ArrayList<>();
            model.addAttribute("images", imageDtos);

            return "user/main";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Add error message to model
            model.addAttribute("error", "An error occurred while loading the page. Please try again later.");
            // Return the template with error message
            return "user/main";
        }
    }

    @GetMapping("user/cart")
    public String usersCart(Authentication authentication, Model model) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }

            Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
            if (currentUser.isEmpty()) {
                return "redirect:/api/auth/login";
            }

            User user = currentUser.get();
            
            // Get cart items for the user
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            
            // Calculate totals
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
            // Log the error
            e.printStackTrace();
            // Add error message to model
            model.addAttribute("error", "An error occurred while loading the cart. Please try again later.");
            // Return the template with error message
            return "user/cart";
        }
    }

    @GetMapping("user/checkout")
    public String usersCheckout(Authentication authentication, Model model) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }

            Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
            if (currentUser.isEmpty()) {
                return "redirect:/api/auth/login";
            }

            User user = currentUser.get();
            
            // Get cart items for the user
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            
            // Calculate totals
            double subtotal = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            double shipping = subtotal > 0 ? 45.0 : 0.0; // Example shipping cost
            double total = subtotal + shipping;

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shipping", shipping);
            model.addAttribute("total", total);
            model.addAttribute("user", user);

        return "user/checkout";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while loading the checkout page. Please try again later.");
            return "user/checkout";
        }
    }

    @GetMapping("user/contact")
    public String usersContact(Authentication authentication, Model model) {

        return "/user/contact";
    }

    @GetMapping("user/shop")
    public String usersShop(Authentication authentication, Model model) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }

            Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
            if (currentUser.isEmpty()) {
                return "redirect:/api/auth/login";
            }

            // Get all products
            List<Product> products = productService.getAllProducts();
            if (products == null) {
                products = new ArrayList<>();
            }
            
            // Get all categories
            List<Category> categories = Arrays.asList(Category.values());
            
            // Get all product images
            List<ProductImage> images = productImageService.getAllImages();
            List<ImageDTO> imageDtos = images != null ? 
                images.stream()
                    .map(ProductImage::toDTO)
                    .collect(Collectors.toList()) : 
                new ArrayList<>();

            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("cats", categories); // Keep for backward compatibility
            model.addAttribute("images", imageDtos);
            model.addAttribute("currentUser", currentUser.get());
            
            return "user/shop";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Add error message to model
            model.addAttribute("error", "An error occurred while loading the shop. Please try again later.");
            // Return the template with error message
            return "user/shop";
        }
    }

    @GetMapping("user/single-product")
    public String usersSingleProduct(Authentication authentication, Model model) {

        return "/user/single-product";
    }

    @GetMapping("user/wishList")
    public String usersWhisList(Authentication authentication, Model model) {

        return "/user/wishlist";

    }

    @GetMapping("user/profile")
    public String userProfile(Authentication authentication, Model model) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }

            Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
            if (currentUser.isEmpty()) {
                return "redirect:/api/auth/login";
            }

            User user = currentUser.get();
            
            // Get user's orders
            List<Order> userOrders = orderService.getOrdersByUser(user.getId());
            
            model.addAttribute("user", user);
            model.addAttribute("orders", userOrders);

            return "user/profile";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Add error message to model
            model.addAttribute("error", "An error occurred while loading the profile. Please try again later.");
            // Return the template with error message
            return "user/profile";
        }
    }

    @GetMapping("user/single-product/{id}")
    public String singleProduct(@PathVariable Integer id, Authentication authentication, Model model) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }

            Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
            if (currentUser.isEmpty()) {
                return "redirect:/api/auth/login";
            }

            // Get the product by ID
            Product product = productService.getProductById(id);
            if (product == null) {
                model.addAttribute("error", "Product not found");
                return "redirect:/user/main";
            }

            // Get related products (same category, excluding current product)
            List<Product> relatedProducts = productService.getAllProducts().stream()
                    .filter(p -> p.getCategory() == product.getCategory() && !p.getId().equals(id))
                    .limit(4)
                    .collect(Collectors.toList());

            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", relatedProducts);
            model.addAttribute("currentUser", currentUser.get());

            return "user/single-product";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Add error message to model
            model.addAttribute("error", "An error occurred while loading the product. Please try again later.");
            // Return the template with error message
            return "redirect:/user/main";
        }
    }

  }