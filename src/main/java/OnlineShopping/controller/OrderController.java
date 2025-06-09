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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        try {
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
        } catch (Exception e) {
            System.err.println("Error in adminOrders: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/adminDashboard";
        }
    }

    @GetMapping("/delivery")
    public String deliveryOrders(Model model, Authentication authentication) {
        try {
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
        } catch (Exception e) {
            System.err.println("Error in deliveryOrders: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/api/auth/login";
        }
    }

    @PostMapping("/{orderId}/status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status,
            Authentication authentication) {
        try {
            System.out.println("=== ORDER CONTROLLER: Updating order status ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("New Status: " + status);
            
            // Validate authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Authentication failed");
                return ResponseEntity.status(401).body(createErrorResponse("Not authenticated"));
            }

            // Get user
            User user = userService.findByEmail(authentication.getName());
            if (user == null) {
                System.out.println("User not found");
                return ResponseEntity.status(401).body(createErrorResponse("User not found"));
            }

            System.out.println("User: " + user.getUsername() + " (Role: " + user.getRole() + ")");

            // Validate user permissions
            if (user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.DELIVERY) {
                System.out.println("Unauthorized access attempt");
                return ResponseEntity.status(403).body(createErrorResponse("Unauthorized - Only admin and delivery users can update order status"));
            }

            // Validate delivery user restrictions
            if (user.getRole() == User.Role.DELIVERY && status != Order.OrderStatus.DELIVERED) {
                System.out.println("Delivery user attempted to set invalid status: " + status);
                return ResponseEntity.status(403).body(createErrorResponse("Delivery users can only mark orders as delivered"));
            }

            // Validate order ID
            if (orderId == null || orderId <= 0) {
                System.out.println("Invalid order ID: " + orderId);
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid order ID"));
            }

            // Validate status
            if (status == null) {
                System.out.println("Invalid status: null");
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid order status"));
            }

            System.out.println("Updating order status...");
            
            // Update order status
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            
            System.out.println("Order status updated successfully");
            System.out.println("Updated Order ID: " + updatedOrder.getId());
            System.out.println("New Status: " + updatedOrder.getStatus());

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order status updated successfully");
            response.put("orderId", updatedOrder.getId());
            response.put("newStatus", updatedOrder.getStatus().name());
            response.put("updatedAt", updatedOrder.getUpdatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            System.err.println("=== ORDER CONTROLLER: Validation error ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorResponse("Validation error: " + e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("=== ORDER CONTROLLER: Runtime error ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorResponse("Order processing error: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("=== ORDER CONTROLLER: Unexpected error ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("An unexpected error occurred while updating order status"));
        }
    }

    @GetMapping("/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        try {
            System.out.println("=== ORDER CONTROLLER: Getting order details ===");
            System.out.println("Order ID: " + orderId);
            
            if (orderId == null || orderId <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid order ID"));
            }

            Order order = orderService.getOrderById(orderId);
            
            System.out.println("Order found: " + order.getId());
            
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorResponse("Validation error: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error retrieving order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Error retrieving order: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
} 