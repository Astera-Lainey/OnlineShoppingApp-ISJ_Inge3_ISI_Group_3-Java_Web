package OnlineShopping.repository;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.Review;
import OnlineShopping.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
    List<Review> findByItem(Item item);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.item = ?1")
    Double findAverageRatingByItem(Item item);

    List<Review> findByItemOrderByDateDesc(Item item);
    List<Review> findByRatingGreaterThanEqual(Integer rating);
}
