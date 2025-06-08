package OnlineShopping.service;

import OnlineShopping.entity.CartItem;
import OnlineShopping.entity.Product;
import OnlineShopping.entity.User;
import OnlineShopping.entity.WishList;
import OnlineShopping.entity.repository.UserRepository;
import OnlineShopping.entity.repository.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service

public class WishListService {

    @Autowired
    WishListRepository wishListRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductService productService;

    public WishList addToList(String userEmail, Integer productId) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();

        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        //Check if product is already in wish list
        List<WishList> existingItems = wishListRepository.findByUser(user);
        for (WishList item : existingItems) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }

        //Create new wish list item
        WishList wishList = new WishList();
        wishList.setUser(user);
        wishList.setProduct(product);
        wishListRepository.save(wishList);
        return wishList;
    }

    public List<WishList> getWishListItems(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return wishListRepository.findByUser(userOpt.get());
    }

    public void removeFromWishList(Integer wishListId) {
        Optional<WishList> wishListOpt = wishListRepository.findByWishListId(wishListId);
        if (wishListOpt.isEmpty()) {
            throw new RuntimeException("Wishlist item not found");
        }
        wishListRepository.delete(wishListOpt.get());
    }

    public CartItem updateWishList(Long cartId, Integer quantity, Integer wishListId) {
        Optional<WishList> wishListOpt = wishListRepository.findByWishListId(wishListId);
        if (wishListOpt.isPresent()) {
            wishListRepository.delete(wishListOpt.get());
        }
        return null;
    }
}

