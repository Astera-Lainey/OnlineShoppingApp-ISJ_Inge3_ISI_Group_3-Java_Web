package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    public Integer orderId;
    public int quantity;
    public Double totalPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    public Product product;

}
