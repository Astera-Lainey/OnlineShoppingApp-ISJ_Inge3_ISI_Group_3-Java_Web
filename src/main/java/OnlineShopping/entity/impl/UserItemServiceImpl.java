package OnlineShopping.entity.impl;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.ItemCategory;
import OnlineShopping.entity.repository.ItemCategoryRepository;
import OnlineShopping.entity.repository.ItemRepository;
import OnlineShopping.entity.services.UserItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserItemServiceImpl implements UserItemService {

    private final ItemRepository itemRepository;
    private final ItemCategoryRepository categoryRepository;

    @Autowired
    public UserItemServiceImpl(ItemRepository itemRepository, ItemCategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));
    }

    @Override
    public List<Item> getItemsByCategory(Long categoryId) {
        ItemCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        return itemRepository.findByCategory(category);
    }

    @Override
    public List<Item> searchItems(String keyword) {
        return itemRepository.findByItemNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Item> getItemsByPriceRange(Double minPrice, Double maxPrice) {
        return List.of();
    }

    @Override
    public List<Item> getItemsByPrice(Double price) {
        return itemRepository.findByPrice(price);
    }

    @Override
    public List<Item> getTopRatedItems() {
        return itemRepository.findTopRatedItems();
    }
}