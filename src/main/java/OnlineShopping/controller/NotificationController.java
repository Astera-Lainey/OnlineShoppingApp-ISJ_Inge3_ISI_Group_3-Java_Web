package OnlineShopping.controller;

import OnlineShopping.entity.Notification;
import OnlineShopping.entity.User;
import OnlineShopping.service.NotificationService;
import OnlineShopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Get user's notifications page
     */
    @GetMapping("/user")
    public String getUserNotifications(Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/api/auth/login";
            }
            
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return "redirect:/api/auth/login";
            }
            
            List<Notification> notifications = notificationService.getUserNotifications(user.getId());
            long unreadCount = notificationService.getUnreadNotificationCount(user.getId());
            
            model.addAttribute("notifications", notifications);
            model.addAttribute("unreadCount", unreadCount);
            model.addAttribute("user", user);
            
            return "user/notifications";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while loading notifications");
            return "user/notifications";
        }
    }
    
    /**
     * Get unread notifications count (for header badge)
     */
    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<?> getUnreadCount() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.ok(Map.of("count", 0));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.ok(Map.of("count", 0));
            }
            
            long unreadCount = notificationService.getUnreadNotificationCount(user.getId());
            
            return ResponseEntity.ok(Map.of("count", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("count", 0));
        }
    }
    
    /**
     * Get recent notifications (for dropdown)
     */
    @GetMapping("/recent")
    @ResponseBody
    public ResponseEntity<?> getRecentNotifications(@RequestParam(defaultValue = "5") int limit) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.ok(Map.of("notifications", List.of()));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.ok(Map.of("notifications", List.of()));
            }
            
            List<Notification> notifications = notificationService.getRecentNotifications(user.getId(), limit);
            
            return ResponseEntity.ok(Map.of("notifications", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mark notification as read
     */
    @PostMapping("/mark-read/{notificationId}")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            Notification notification = notificationService.getNotificationById(notificationId);
            if (!notification.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only mark your own notifications as read"));
            }
            
            Notification updatedNotification = notificationService.markAsRead(notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification marked as read");
            response.put("notification", updatedNotification);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mark all notifications as read
     */
    @PostMapping("/mark-all-read")
    @ResponseBody
    public ResponseEntity<?> markAllAsRead() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            notificationService.markAllAsRead(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All notifications marked as read");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a notification
     */
    @DeleteMapping("/delete/{notificationId}")
    @ResponseBody
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            Notification notification = notificationService.getNotificationById(notificationId);
            if (!notification.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only delete your own notifications"));
            }
            
            notificationService.deleteNotification(notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get review-related notifications
     */
    @GetMapping("/reviews")
    @ResponseBody
    public ResponseEntity<?> getReviewNotifications() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            
            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }
            
            List<Notification> reviewNotifications = notificationService.getReviewNotifications(user.getId());
            
            return ResponseEntity.ok(Map.of("notifications", reviewNotifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 