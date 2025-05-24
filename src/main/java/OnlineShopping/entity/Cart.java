package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    public String cartId;
    @OneToOne(fetch = FetchType.LAZY)
    public Customer customer;
}
