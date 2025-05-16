package OnlineShopping.services;

import OnlineShopping.entity.Item;

import java.util.List;

public interface AdminItemService {
    Item createItem(Item item);
    Item editItem(Long itemId, Item updatedItem);
    void deleteItem(Long itemId);

    List<Item> getAllItems();
    Item getItemById(Long itemId);
    List<Item> getItemsByCategory(Long categoryId);
}