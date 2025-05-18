package OnlineShopping.entity.services;

import OnlineShopping.entity.Stock;
import java.util.List;

public interface AdminStockService {
    Stock updateStock(Long itemId, Integer quantity);
    Stock getStockByItemId(Long itemId);
    List<Stock> getAllStocks();
    List<Stock> getLowStockItems(Integer threshold);
}