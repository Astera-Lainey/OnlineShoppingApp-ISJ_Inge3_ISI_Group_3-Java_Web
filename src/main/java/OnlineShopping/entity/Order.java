package OnlineShopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    public Integer orderId;
    @ManyToOne
    public Customer customer;
    public Status orderStatus;
    public Date orderDate;
    public double totalPrice;
}
