package OnlineShopping.entity.repository;

import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository for Product entity
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
//    Long id(int id);

//    boolean existsByName(String name);
    List<Product> findAllByCategory(Category category);
    List<Product> findTop8ByOrderByIdDesc();

    Optional<Product> findById(Integer id);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByNameContainingIgnoreCase(Category category);


    boolean existsByName(String name);

    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR p.category = :category)")
    List<Product> searchProducts(@Param("name") String name,
                                 @Param("category") Category category);

    List<Product> findByNameContainingAndCategory(String name, Category category);

    List<Product> findByNameContaining(String name);

    List<Product> findByCategory(Category category);
}

