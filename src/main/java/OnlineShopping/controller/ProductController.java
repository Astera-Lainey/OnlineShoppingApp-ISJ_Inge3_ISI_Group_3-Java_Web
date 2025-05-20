package OnlineShopping.controller;

import OnlineShopping.dto.ProductDTO;
import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import OnlineShopping.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public String adminProducts(Model model) {
        List<Product> allProducts = productService.ViewProducts();
        model.addAttribute("products", allProducts);
        model.addAttribute("productform", new ProductDTO());
        model.addAttribute("cats",Category.values());
        return "/admin/Product";
    }

    @PostMapping("/add")
    public String CreateProduct(@ModelAttribute("productform") ProductDTO productDTO, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        System.out.println("Product Details received for: " + productDTO.getName());
        if (bindingResult.hasErrors()) {
            System.out.println("These errors occured" + bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getAllErrors());
            return "/admin/Product";
        }
        try{
            productService.createProduct(productDTO.getName(),productDTO.getDescription(),productDTO.getImages(),productDTO.getBrand(),productDTO.getCategory());
            System.out.println("Product created successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/products";
    }




}
