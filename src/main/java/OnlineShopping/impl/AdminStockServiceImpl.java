package OnlineShopping.impl;

import OnlineShopping.entity.Item;
import OnlineShopping.entity.Stock;
import OnlineShopping.repository.ItemRepository;
import OnlineShopping.repository.StockRepository;
import OnlineShopping.services.AdminStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminStockServiceImpl implements AdminStockService {

    private final StockRepository stockRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public AdminStockServiceImpl(StockRepository stockRepository, ItemRepository itemRepository) {
        this.stockRepository = stockRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Stock updateStock(Long itemId, Integer quantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        Optional<Stock> existingStockOpt = stockRepository.findByItem(item);

        Stock stock;
        if (existingStockOpt.isPresent()) {
            stock = existingStockOpt.get();
            stock.setQuantity(quantity);
        } else {
            // Create new stock entry if not exists
            stock = new Stock();
            stock.setItem(item);
            stock.setQuantity(quantity);
        }

        return stockRepository.save(stock);
    }

    @Override
    public Stock getStockByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        return stockRepository.findByItem(item)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found for item with ID: " + itemId));
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public List<Stock> getLowStockItems(Integer threshold) {
        return stockRepository.findByQuantityLessThan(threshold);
    }
}