package OnlineShopping.entity.repository;

import OnlineShopping.entity.Product;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for Product entity
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Long id(int id);

    boolean existsByName(String name);
}

