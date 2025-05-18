package OnlineShopping.entity.impl;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.ItemCategory;
import OnlineShopping.entity.Stock;
import OnlineShopping.entity.repository.ItemCategoryRepository;
import OnlineShopping.entity.repository.ItemRepository;
import OnlineShopping.entity.repository.StockRepository;
import OnlineShopping.entity.services.AdminItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminItemServiceImpl implements AdminItemService {

    private final ItemRepository itemRepository;
    private final ItemCategoryRepository categoryRepository;
    private final StockRepository stockRepository;

    @Autowired
    public AdminItemServiceImpl(ItemRepository itemRepository,
                                ItemCategoryRepository categoryRepository,
                                StockRepository stockRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public Item createItem(Item item) {
        Item savedItem = itemRepository.save(item);

        // Create initial stock entry for the item
        Stock stock = new Stock();
        stock.setItem(savedItem);
        stock.setQuantity(0); // Default initial quantity
        stockRepository.save(stock);

        return savedItem;
    }

    @Override
    @Transactional
    public Item editItem(Long itemId, Item updatedItem) {
        Optional<Item> existingItemOpt = itemRepository.findById(itemId);

        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();

            // Update fields
            existingItem.setItemName(updatedItem.getItemName());
//            existingItem.setDescription(updatedItem.getDescription());
//            existingItem.setPrice(updatedItem.getPrice());
//            existingItem.setImageUrl(updatedItem.getImageUrl());

            // Update category if provided
            if (updatedItem.getCategory() != null) {
                existingItem.setCategory(updatedItem.getCategory());
            }

            return itemRepository.save(existingItem);
        } else {
            throw new IllegalArgumentException("Item not found with ID: " + itemId);
        }
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);

        if (itemOpt.isPresent()) {
            // First delete associated stock
            Optional<Stock> stockOpt = stockRepository.findByItem(itemOpt.get());
            stockOpt.ifPresent(stockRepository::delete);

            // Then delete the item
            itemRepository.deleteById(itemId);
        } else {
            throw new IllegalArgumentException("Item not found with ID: " + itemId);
        }
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
}