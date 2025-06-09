package OnlineShopping.service;

import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
<<<<<<< HEAD
import OnlineShopping.entity.repository.ProductImageRepository;
=======
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
import OnlineShopping.entity.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;
=======
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageService productImageService;

    public Product createProduct( String name, String description, String brand, Category category, double price, int stock){
        if(productRepository.existsByName(name)){
            throw new RuntimeException("Product already exists");
        }
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        product.setStockQuantity(stock);
        return productRepository.save(product);
    };
    public List<Product> getAllProducts( ){
        return productRepository.findAll();
    };
    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }
    public Product getProductById(int id) {
<<<<<<< HEAD
        return productRepository.findById(id);
=======
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
    }
    public Product updateProduct(Product product){
        return productRepository.save(product);
    }
    @Transactional
    public void deleteProduct(Product product){

            List<ProductImage> images = productImageService.getImagesByProductId(product.getId());
            if (!images.isEmpty()){
                for (ProductImage image : images) {

                    // Delete physical files
                    try {
                        Path path = Paths.get(image.getPath());
                        if (Files.exists(path)) Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                productImageService.deleteAllImages(images);
            }

            productRepository.delete(product);
    }
    public Product updateProduct(Integer productId, int stockQuantity){
<<<<<<< HEAD
        Product product = productRepository.findById(productId);
=======
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
        product.setStockQuantity(stockQuantity);
        return productRepository.save(product);
    }

}

