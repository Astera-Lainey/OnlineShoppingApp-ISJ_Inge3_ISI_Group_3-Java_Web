package OnlineShopping.controller;

import OnlineShopping.dto.ImageDTO;
import OnlineShopping.dto.ProductDTO;
import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.service.ProductImageService;
import OnlineShopping.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class DashboardController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    //admin getMappings
    @GetMapping("admin/adminDashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }

        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser.isEmpty()) {
            return "redirect:/api/auth/login";
        }
        List<Product> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);
        model.addAttribute("productform", new ProductDTO());
        model.addAttribute("cats", Category.values());

        // Add admin-specific statistics
        model.addAttribute("totalUsers", userRepository.count());
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
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }
//        Object user;
//        if (authentication.getPrincipal() != null) {
//            user = authentication.getPrincipal();
//            if(user == null){
//                logger.error("user principal is null");
//            }
//            assert user != null;
//            logger.info("user principal is {}", user.toString());
//            model.addAttribute("user", user);
//            // Add other attributes as needed
//        } else {
//            logger.error("authentication.getPrincipal() is null");
//        }
//        Models

        List<Product> products = productService.getAllProducts();
        List<ProductImage> images = productImageService.getAllImages();
        List<ImageDTO> imagedto = new ArrayList<>();
        for (ProductImage image : images) {
            imagedto.add(image.toDTO());
        }
        model.addAttribute("products", products);
        model.addAttribute("cats", Category.values());
        model.addAttribute("images", imagedto );

        Optional<User> currentUserOpt = userRepository.findByEmail(authentication.getName());
        if (currentUserOpt.isEmpty()) {
            logger.info("No user with email {}", authentication.getName());
            return "redirect:/api/auth/login";
        } else {
            logger.info("User found in db {}",  currentUserOpt.get());
            model.addAttribute("user", currentUserOpt.get());
        }

//        User currentUser = currentUserOpt.get();

        return "/user/main";
    }


    @GetMapping("user/checkout")
    public String usersCheckout(Authentication authentication, Model model) {

        return "/user/checkout";
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

    //        product Models
    @GetMapping("/view")
    public String productsAndImages(@RequestParam("ProductId") int ProductId, Model model){
        List<ProductImage> images = productImageService.getImagesByProductId(ProductId);
        List<ImageDTO> imagedto = new ArrayList<>();
        for (ProductImage image : images) {
            imagedto.add(image.toDTO());
        }

        model.addAttribute("images", imagedto);

        return "admin/adminDashboard";
    }


  }