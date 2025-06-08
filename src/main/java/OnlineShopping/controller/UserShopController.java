package OnlineShopping.controller;

import OnlineShopping.entity.Product;
import OnlineShopping.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
    @RequestMapping("/user")
    public class UserShopController {

        @Autowired
        private ProductService productService;

        @GetMapping("/shop-page")
        public String showShop(Model model) {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            return "shop"; // returns shop.html
        }

        @GetMapping("/main-page")
        public String showMainPage(Model model) {
            // Get featured products or latest products for main page
            List<Product> featuredProducts = productService.getFeaturedProducts(); // or getLatestProducts()
            model.addAttribute("products", featuredProducts);
            return "index"; // your main page template
        }
    }

