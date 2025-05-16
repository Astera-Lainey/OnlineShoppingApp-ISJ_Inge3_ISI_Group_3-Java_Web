package OnlineShopping.repository;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(ItemCategory category);

    List<Item> findByItemNameContainingIgnoreCase(String keyword);

    List<Item> findByPrice(Double price);

    @Query("SELECT i FROM Item i JOIN i.reviews r GROUP BY i ORDER BY AVG(r.rating) DESC")
    List<Item> findTopRatedItems();

//    List<Item> findByCategoryAndPriceBetween(ItemCategory category, Double minPrice, Double maxPrice);
}