package OnlineShopping.controller;

import OnlineShopping.entity.StockNotification;
import OnlineShopping.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/admin")
public class AdminInventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/notifications")
    @ResponseBody
    public List<StockNotification> getNotifications() {
        return inventoryService.getUnreadNotifications();
    }

    @GetMapping("/notifications/count")
    @ResponseBody
    public long getUnreadNotificationCount() {
        return inventoryService.getUnreadNotificationCount();
    }

    @PostMapping("/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id) {
        inventoryService.markNotificationAsRead(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification marked as read");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notifications/read-all")
    @ResponseBody
    public ResponseEntity<?> markAllNotificationsAsRead() {
        inventoryService.markAllNotificationsAsRead();
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/{id}/stock")
    @ResponseBody
    public ResponseEntity<?> updateStock(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer newStock = request.get("stockQuantity");
            if (newStock == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Stock quantity is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (newStock < 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Stock quantity cannot be negative");
                return ResponseEntity.badRequest().body(error);
            }
            
            inventoryService.updateStock(id, newStock);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Stock updated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred while updating stock");
            return ResponseEntity.internalServerError().body(error);
        }
    }
} 