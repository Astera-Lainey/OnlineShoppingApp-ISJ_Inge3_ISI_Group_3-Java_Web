package OnlineShopping.entity;

<<<<<<< HEAD
=======
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
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
<<<<<<< HEAD
    @OneToOne(fetch = FetchType.LAZY)
    public Product product;
    public int quantity;
    public double price;
=======
    
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
    
    // Helper method to calculate total price
    public double getTotalPrice() {
        return price * quantity;
    }
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
}
