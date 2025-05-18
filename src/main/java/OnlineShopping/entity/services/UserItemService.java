package OnlineShopping.entity.services;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.ItemCategory;

import java.util.List;

public interface UserItemService {
    List<Item> getAllItems();
    Item getItemById(Long itemId);
    List<Item> getItemsByCategory(Long categoryId);
    List<Item> searchItems(String keyword);
    List<Item> getItemsByPriceRange(Double minPrice, Double maxPrice);

    List<Item> getItemsByPrice(Double price);

    List<Item> getTopRatedItems();
}