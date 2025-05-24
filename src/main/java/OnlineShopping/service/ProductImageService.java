package OnlineShopping.service;

import OnlineShopping.entity.ProductImage;
import OnlineShopping.entity.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    public ProductImage addProductImage(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }
}
