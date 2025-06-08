package OnlineShopping.entity.repository;

import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import OnlineShopping.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Integer> {
    List<WishList> findByUserAndProduct(User user, Product product);
    List<WishList> findByUser(User user);
    Optional<WishList> findByWishListId(Integer wishListId);
}
