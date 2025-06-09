package OnlineShopping.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer cartItemId;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    public Cart cart;
    
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    public Product product;
    
    public int quantity;
    public double price;
    public double totalPrice;
    
    // Helper method to calculate total price
    public double getTotalPrice() {
        return price * quantity;
    }
    
    // Setter to update totalPrice when price or quantity changes
    public void setPrice(double price) {
        this.price = price;
        this.totalPrice = price * quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }
}
