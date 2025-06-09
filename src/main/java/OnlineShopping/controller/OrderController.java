package OnlineShopping.controller;

import OnlineShopping.entity.Order;
import OnlineShopping.entity.User;
import OnlineShopping.service.OrderService;
import OnlineShopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String adminOrders(
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) String search,
            Model model,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null || user.getRole() != User.Role.ADMIN) {
            return "redirect:/api/auth/login";
        }

        // Get orders based on status filter
        List<Order> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            // Default to pending orders if no status specified
            orders = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);
        }

        // TODO: Implement search functionality if needed
        // For now, we'll just pass the orders to the model
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status != null ? status : Order.OrderStatus.PENDING);
        
        // Return to admin dashboard instead of a separate orders page
        return "redirect:/admin/adminDashboard";
    }

    @GetMapping("/delivery")
    public String deliveryOrders(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/api/auth/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null || user.getRole() != User.Role.DELIVERY) {
            return "redirect:/api/auth/login";
        }

        List<Order> orders = orderService.getOrdersByStatus(Order.OrderStatus.SHIPPING);
        model.addAttribute("orders", orders);
        return "delivery/orders";
    }

    @PostMapping("/{orderId}/status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.badRequest().body("Not authenticated");
            }

            User user = userService.findByEmail(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            // Only admin and delivery users can update order status
            if (user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.DELIVERY) {
                return ResponseEntity.badRequest().body("Unauthorized");
            }

            // Delivery users can only update orders to DELIVERED
            if (user.getRole() == User.Role.DELIVERY && status != Order.OrderStatus.DELIVERED) {
                return ResponseEntity.badRequest().body("Delivery users can only mark orders as delivered");
            }

            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating order status: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving order: " + e.getMessage());
        }
    }
} 