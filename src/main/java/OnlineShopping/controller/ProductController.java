package OnlineShopping.controller;

import OnlineShopping.dto.ImageDTO;
import OnlineShopping.dto.ProductDTO;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
import OnlineShopping.entity.Review;
import OnlineShopping.entity.User;
import OnlineShopping.service.ProductImageService;
import OnlineShopping.service.ProductService;
import OnlineShopping.service.ReviewService;
import OnlineShopping.service.UserService;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public String CreateProduct(@ModelAttribute("productform") ProductDTO productDTO, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        System.out.println("Product Details received for: " + productDTO.getName());
        try {
            //creates a product
            Product newProduct = productService.createProduct(productDTO.getName(), productDTO.getDescription(), productDTO.getBrand(), productDTO.getCategory(), productDTO.getPrice(), productDTO.getStock(), productDTO.getClothesSize(), productDTO.getShoeSize(), productDTO.getColor());

            //creates an image for each image sent for a specific product
            List<MultipartFile> images = productDTO.getImage();
            productImageService.addProductImages(images, newProduct);
            System.out.println("Product created successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
            return "redirect:/admin/adminDashboard/{userId}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/adminDashboard/{userId}";
    }

    @PostMapping("/delete/{name}")
    public String deleteProduct(
            @PathVariable String name,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Product product = productService.getProductByName(name);
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
                return "redirect:/admin/adminDashboard/{userId}";
            }
            productService.deleteProduct(product);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Product and images deleted successfully"
            );
        } catch (Exception e) {  // Combined exception handling
            log.error("Delete failed for product '{}'", name, e);
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Deletion error: " + e.getMessage()
            );
        }

        return "redirect:/admin/adminDashboard/{userId}";
    }

    @PostMapping("/updatePhotos")
    public String updatePhotos(@RequestParam("productId") Integer productId,
                               @RequestParam("images") List<MultipartFile> images,
                               RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId);
            productImageService.addProductImages(images, product);
            redirectAttributes.addFlashAttribute("successMessage", "Product photos updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update photos: " + e.getMessage());
        }
        return "redirect:/admin/adminDashboard/{userId}";
    }

    @PostMapping("/deleteImage/{id}")
    public String deleteImage(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productImageService.deleteImage(id);
        redirectAttributes.addFlashAttribute("successMessage", "Image deleted successfully");
        return "redirect:/admin/adminDashboard/{userId}";
    }

    @PostMapping("/update/{id}")
    public String updateProductStock(@PathVariable Integer id,
                                     @RequestParam int stockQuantity) {
        productService.updateProduct(id, stockQuantity);
        return "redirect:/admin/adminDashboard/{userId}";
    }

    @GetMapping("/user/single-product/{productId}")
    public String usersSingleProduct(Authentication authentication, Model model, @PathVariable Integer productId) {
        Product p = productService.getProductById(productId);
        List<ProductImage> images = productImageService.getImagesByProductId(productId);
        List<ImageDTO> imagedto = new ArrayList<>();
        for (ProductImage image : images) {
            try {
                ImageDTO dto = image.toDTO();
                if (dto != null && dto.getProductId() != null) { // Ensure productId is valid
                    imagedto.add(dto);
                } else {
                    // Fallback if toDTO() fails or product is not initialized
                    imagedto.add(ImageDTO.builder()
                            .imageId(image.getId())
                            .imageUrl(image.getPath())
                            .productId(p != null ? p.getId() : null) // Use product from model if available
                            .build());
                }
            } catch (Exception e) {
                log.error("Error converting ProductImage to DTO: {}", e.getMessage());
                // Fallback with basic mapping
                imagedto.add(ImageDTO.builder()
                        .imageId(image.getId())
                        .imageUrl(image.getPath())
                        .productId(p != null ? p.getId() : null)
                        .build());
            }
        }
        model.addAttribute("product", p);
        model.addAttribute("images", imagedto);
        model.addAttribute("productId", productId);
        model.addAttribute("reviews", reviewService.findByProductId(productId));
        model.addAttribute("userId", authentication != null ? authentication.getName() : "Anonymous");
        return "/user/single-product";
    }

    @PostMapping("/add-review")
    public String addReview(@RequestParam Integer productId, @RequestParam String comment, @RequestParam int rating, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            Review review = new Review();
            review.setComment(comment);
            if (authentication != null) {
                User user = userService.findByEmail(authentication.getName());
                if (user != null) {
                    review.setUser(user);
                } else {
                    throw new Exception("User not found for email: " + authentication.getName());
                }
            } else {
                throw new Exception("No authenticated user found");
            }
            review.setRating(rating);
            review.setReviewDate(LocalDate.now());
            Product product = productService.getProductById(productId);
            if (product != null) {
                review.setProduct(product);
            }
            reviewService.saveReview(review);
            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit review: " + e.getMessage());
        }
        return "redirect:/user/single-product/" + productId;
    }
}