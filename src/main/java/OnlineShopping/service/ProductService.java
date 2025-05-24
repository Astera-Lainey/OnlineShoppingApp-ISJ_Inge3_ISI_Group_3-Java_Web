package OnlineShopping.service;

import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct( String name, String description, String brand, Category category, double price, int stock){
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        product.setStockQuantity(stock);
        return productRepository.save(product);
    };
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    };

    public Product updateProduct(Product product){
        return productRepository.save(product);
    }
}

