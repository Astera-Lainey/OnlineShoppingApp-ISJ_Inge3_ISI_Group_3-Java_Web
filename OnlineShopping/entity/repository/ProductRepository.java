package OnlineShopping.entity.repository;

import OnlineShopping.entity.Category;
import OnlineShopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
<<<<<<< HEAD

// Repository for Product entity
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
=======
import java.util.Optional;

// Repository for Product entity
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
//    Long id(int id);

//    boolean existsByName(String name);
    List<Product> findAllByCategory(Category category);
<<<<<<< HEAD
    Product findById(Integer id);
=======
    Optional<Product> findById(Integer id);
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38

    boolean existsByName(String name);

    Product findByName(String name);
}

