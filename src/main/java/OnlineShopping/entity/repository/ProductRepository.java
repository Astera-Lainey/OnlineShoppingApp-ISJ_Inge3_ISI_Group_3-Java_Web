package OnlineShopping.entity.repository;

import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for Product entity
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
//    Long id(int id);

//    boolean existsByName(String name);
    List<Product> findAllByCategory(Category category);
    Optional<Product> findById(Integer id);

    boolean existsByName(String name);

    Product findByName(String name);
}

