package OnlineShopping.entity.repository;

import OnlineShopping.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for ProductImage entity
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findAllByProduct_Id(Integer productId);

    Optional<Object> findById(Integer id);

    void deleteById(Integer id);

    void deleteByProductId(Integer productId);

    List<ProductImage> findByProductId(Integer productId);
}
