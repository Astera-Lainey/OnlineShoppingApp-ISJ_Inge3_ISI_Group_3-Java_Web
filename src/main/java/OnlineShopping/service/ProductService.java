package OnlineShopping.service;

import OnlineShopping.dto.ProductDTO;
import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

   public void createProduct( String name, String description, List<MultipartFile> images, String brand, Category category );
   public List<Product> ViewProducts();
}

