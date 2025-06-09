package OnlineShopping.controller;

import OnlineShopping.dto.ImageDTO;
import OnlineShopping.dto.ProductDTO;
<<<<<<< HEAD
import OnlineShopping.entity.Category;
=======
import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Category;
import OnlineShopping.entity.Order;
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.UserRepository;
<<<<<<< HEAD
import OnlineShopping.service.ProductImageService;
import OnlineShopping.service.ProductService;
=======
import OnlineShopping.service.CartService;
import OnlineShopping.service.ProductImageService;
import OnlineShopping.service.ProductService;
import OnlineShopping.service.OrderService;
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.RequestParam;
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38

import java.util.ArrayList;
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

<<<<<<< HEAD
    //admin getMappings
    @GetMapping("admin/adminDashboard")
    public String adminDashboard(Model model, Authentication authentication) {
=======
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    //admin getMappings
    @GetMapping("admin/adminDashboard")
    public String adminDashboard(
            @RequestParam(required = false) Order.OrderStatus status,
            Model model,
            Authentication authentication) {
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }

        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser.isEmpty()) {
            return "redirect:/api/auth/login";
        }
<<<<<<< HEAD
//        product Models
=======

        // Add orders to the model based on status filter
        List<Order> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            // Default to pending orders if no status specified
            orders = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);
        }
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status != null ? status : Order.OrderStatus.PENDING);

        // Add existing model attributes
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
        List<Product> allProducts = productService.getAllProducts();
        List<ProductImage> images = productImageService.getAllImages();
        List<ImageDTO> imagedto = new ArrayList<>();
        for (ProductImage image : images) {
            imagedto.add(image.toDTO());
        }
        model.addAttribute("products", allProducts);
        model.addAttribute("productform", new ProductDTO());
        model.addAttribute("cats", Category.values());
<<<<<<< HEAD
        model.addAttribute("images", imagedto );

        // Add admin-specific statistics
        model.addAttribute("totalUsers", userRepository.count());
=======
        model.addAttribute("images", imagedto);

        // Add admin-specific statistics
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalOrders", orderService.getOrdersByStatus(null).size()); // Get all orders for total count
        model.addAttribute("pendingOrders", orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING)
                .count());

>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
        return "admin/adminDashboard";
    }


    @GetMapping("admin/Users")
    public String adminUsers() {

        return "/admin/Users";
    }



    @GetMapping("admin/Orders")
    public String adminOrders() {

        return "/admin/Orders";
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
<<<<<<< HEAD

        return "/user/cart";
=======
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
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
    }

    @GetMapping("user/checkout")
    public String usersCheckout(Authentication authentication, Model model) {
<<<<<<< HEAD

        return "/user/checkout";
=======
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
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
    }

    @GetMapping("user/contact")
    public String usersContact(Authentication authentication, Model model) {

        return "/user/contact";
    }

    @GetMapping("user/shop")
    public String usersShop(Authentication authentication, Model model) {

        return "/user/shop";
    }

    @GetMapping("user/single-product")
    public String usersSingleProduct(Authentication authentication, Model model) {

        return "/user/single-product";
    }

    @GetMapping("user/wishList")
    public String usersWhisList(Authentication authentication, Model model) {

        return "/user/wishlist";

    }



  }