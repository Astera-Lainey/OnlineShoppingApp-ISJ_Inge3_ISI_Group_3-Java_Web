package OnlineShopping.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    public String cartId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    public User customer;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    // Helper method to add an item
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }
    
    // Helper method to remove an item
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
}
