package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String shippingCity;

    @Column(nullable = false)
    private String shippingState;

    @Column(nullable = false)
    private String shippingZipCode;

    @Column(nullable = false)
    private String shippingPhone;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double shippingCost;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = OrderStatus.PENDING;
        if (items == null) {
            items = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (items == null) {
            items = new ArrayList<>();
        }
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        SHIPPING,
        DELIVERED,
        CANCELLED
    }

    public void addItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.remove(item);
        item.setOrder(null);
    }

    public static class OrderBuilder {
        private List<OrderItem> items = new ArrayList<>();
        
        public OrderBuilder items(List<OrderItem> items) {
            this.items = items != null ? items : new ArrayList<>();
            return this;
        }
        
        public Order build() {
            Order order = new Order();
            order.setId(this.id);
            order.setUser(this.user);
            order.setItems(this.items != null ? this.items : new ArrayList<>());
            order.setShippingAddress(this.shippingAddress);
            order.setShippingCity(this.shippingCity);
            order.setShippingState(this.shippingState);
            order.setShippingZipCode(this.shippingZipCode);
            order.setShippingPhone(this.shippingPhone);
            order.setSubtotal(this.subtotal);
            order.setShippingCost(this.shippingCost);
            order.setTotal(this.total);
            order.setStatus(this.status);
            order.setCreatedAt(this.createdAt);
            order.setUpdatedAt(this.updatedAt);
            order.setNotes(this.notes);
            return order;
        }
    }
}
