package OnlineShopping.entity.repository;

import OnlineShopping.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for ProductImage entity
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // You can add custom query methods here if needed
}
