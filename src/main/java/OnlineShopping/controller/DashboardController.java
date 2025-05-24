package OnlineShopping.controller;

import OnlineShopping.entity.User;
import OnlineShopping.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    //admin getMappings
    @GetMapping("/admin/adminDashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }

        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser.isEmpty()) {
            return "redirect:/api/auth/login";
        }

        // Add admin-specific statistics
        model.addAttribute("totalUsers", userRepository.count());
        return "/admin/adminDashboard";
    }

    @GetMapping("/admin/Users")
    public String adminUsers() {

        return "/admin/Users";
    }

//    @GetMapping("/admin/Product")
//    public String adminProducts() {
//
//        return "/admin/Product";
//    }

    @GetMapping("/admin/Orders")
    public String adminOrders() {

        return "/admin/Orders";
    }


    //user getMappings
    @GetMapping("/user/main")
    public String userDashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }

        Optional<User> currentUserOpt = userRepository.findByEmail(authentication.getName());
        if (currentUserOpt.isEmpty()) {
            return "redirect:/api/auth/login";
        }

//        User currentUser = currentUserOpt.get();


        return "/user/main";
    }

    @GetMapping("/user/cart")
    public String usersCart(Authentication authentication, Model model) {

        return "/user/cart";
    }

    @GetMapping("/user/checkout")
    public String usersCheckout(Authentication authentication, Model model) {

        return "/user/checkout";
    }

    @GetMapping("/user/contact")
    public String usersContact(Authentication authentication, Model model) {

        return "/user/contact";
    }

    @GetMapping("/user/shop")
    public String usersShop(Authentication authentication, Model model) {

        return "/user/shop";
    }

    @GetMapping("/user/single-product")
    public String usersSingleProduct(Authentication authentication, Model model) {

        return "/user/single-product";
    }



  }