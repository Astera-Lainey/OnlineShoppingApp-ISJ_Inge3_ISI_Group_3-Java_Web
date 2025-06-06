package OnlineShopping.controller;

import OnlineShopping.dto.ProductDTO;
import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
import OnlineShopping.service.ProductImageService;
import OnlineShopping.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;
    private ProductController cartItemService;


    @PostMapping("/add")
    public String CreateProduct(@ModelAttribute("productform") ProductDTO productDTO, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        System.out.println("Product Details received for: " + productDTO.getName());
            try{
                //creates a product
                Product newProduct = productService.createProduct(productDTO.getName(),productDTO.getDescription(),productDTO.getBrand(),productDTO.getCategory(), productDTO.getPrice(), productDTO.getStock());

                //creates an image for each image sent for a specific product
                List<MultipartFile> images = productDTO.getImage();
                productImageService.addProductImages(images,newProduct);
//                for (MultipartFile image : images) {
//                    try {
//                        //setting the name of the image to the product name
//                        String fileName = productDTO.getName() + "_Image_" + System.currentTimeMillis();
//                        String uploadDir = "uploads/products/images/";
//                        Path uploadPath = Paths.get(uploadDir);
//
//                        //creates the product/images folder in the file system
//                        if (!Files.exists(uploadPath)) {
//                            Files.createDirectories(uploadPath);
//                        }
//
//                        //saves the picture in the folder
//                        try {
//                            // Get file extension from original filename
//                            String originalFilename = image.getOriginalFilename();
//                            String fileExtension = "";
//                            if (originalFilename != null && originalFilename.contains(".")) {
//                                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//                            }
//
//                            String fullFileName = fileName + fileExtension;
//                            Path filePath = uploadPath.resolve(fullFileName);
//                            Files.write(filePath, image.getBytes());
//
//                            log.info("Image saved successfully: {}", filePath.toString());
//
//                            //creates the corresponding image for each images
//                            ProductImage productImage = new ProductImage();
//                            productImage.setPath(uploadDir + fullFileName);
//                            productImage.setProduct(newProduct);
//                            productImageService.saveProductImage(productImage);
//
//                        }catch (Exception e){
//                            log.error(e.getMessage());
//                        }
//
//                    }catch (Exception e){
//                        log.error(e.getMessage());
//                    }
//                }
                System.out.println("Product created successfully");
                redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
                return "redirect:/admin/adminDashboard";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            }
//        }
//        else {
//            redirectAttributes.addFlashAttribute("errorMessage", "Product name already exists");
//        }

        return "redirect:/admin/adminDashboard";    }

    @PostMapping("/delete/{name}")
    public String deleteProduct(
            @PathVariable String name,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Product product = productService.getProductByName(name);
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
                return "redirect:/admin/adminDashboard";
            }


            // Delete database records
//            productImageService.deleteImagesByProductId(product.getId());
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

        return "redirect:/admin/adminDashboard";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam("quantity") Integer quantity,
                            org.springframework.security.core.Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            String userEmail = authentication.getName();
            cartItemService.addToCart(userEmail, productId, quantity != null ? quantity : 1);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add to cart: " + e.getMessage());
        }
        return "redirect:/user/shop";
    }

    private void addToCart(String userEmail, Integer productId, int i) {
    }

    @PostMapping("/updatePhotos")
    public String updatePhotos(@RequestParam("productId") Integer productId,
                               @RequestParam("images") List<MultipartFile> images,
                               RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId);
            productImageService.addProductImages(images,product);
            redirectAttributes.addFlashAttribute("successMessage", "Product photos updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update photos: " + e.getMessage());
        }
        return "redirect:/admin/adminDashboard";
    }

    @PostMapping("/deleteImage/{id}")
    public String deleteImage(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productImageService.deleteImage(id);
        redirectAttributes.addFlashAttribute("successMessage", "Image deleted successfully");
        return "redirect:/admin/adminDashboard";
    }

    @GetMapping("/products/search")
    public String searchProducts(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) Category category,
                                 Model model) {
        List<Product> products = productService.searchProducts(name, category);
        model.addAttribute("products", products);
        return "shop";
    }
    @GetMapping("/shop")
    public String showShop(@RequestParam(required = false) String name,
                           @RequestParam(required = false) Category category,
                           Model model) {
        List<Product> products;

        if (name != null || category != null) {
            // Search was performed
            products = productService.searchProducts(name, category);
        } else {
            // No search parameters, show all products
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        return "shop"; // returns shop.html
    }

    @GetMapping("/main")
    public String showMainPage(Model model) {
        // Get featured products or latest products for main page
        List<Product> featuredProducts = productService.getFeaturedProducts();
        model.addAttribute("products", featuredProducts);
        return "shop"; // your main page template
    }
}
    //@GetMapping("/shop/search")
   // public String shop(@RequestParam(required = false) String name,
                      // @RequestParam(required = false) Category category,
                    //   Model model) {
       // if (name != null || category != null) {
            // Search was performed
            //List<Product> products = productService.searchProducts(name, category);
          //  model.addAttribute("products", products);
       // }
        // If no search parameters, products will be null and default products will show
       // return "shop";
   // }

