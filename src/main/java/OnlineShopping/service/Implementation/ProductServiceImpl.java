package OnlineShopping.service.Implementation;

import OnlineShopping.dto.ProductDTO;
import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
import OnlineShopping.entity.repository.ProductImageRepository;
import OnlineShopping.entity.repository.ProductRepository;
import OnlineShopping.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    @Transactional
    public void createProduct(String name, String description, List<MultipartFile> images, String brand, Category category) {
        if(productRepository.existsByName(name)){
        throw new RuntimeException("The product name already exists");
        }
        else{
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setBrand(brand);
            product.setCategory(category);
            Product savedProduct = productRepository.save(product);

            if (images != null) {
                for (MultipartFile file : images) {
                    try {
                        ProductImage image = new ProductImage();
                        image.setImageData(file.getBytes());
                        image.setImageName(file.getOriginalFilename());
                        image.setContentType(file.getContentType());
                        image.setProduct(savedProduct);

                        productImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store image " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

    }

    @Override
    public List<Product> ViewProducts() {
        return productRepository.findAll();
    }
}

