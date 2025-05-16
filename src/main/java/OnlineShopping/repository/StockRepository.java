package OnlineShopping.repository;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItem(Item item);

    List<Stock> findByQuantityLessThan(Integer threshold);
}