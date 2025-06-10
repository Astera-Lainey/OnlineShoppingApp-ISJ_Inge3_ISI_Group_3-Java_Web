package OnlineShopping.controller;

import OnlineShopping.entity.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/database")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test if we can count reviews
            long reviewCount = reviewRepository.count();
            result.put("success", true);
            result.put("reviewCount", reviewCount);
            result.put("message", "Database connection successful");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("message", "Database connection failed");
        }
        
        return result;
    }
} 