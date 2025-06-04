package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer transactionId;

    @Column(nullable = false)
    public Status status;

    @Column(nullable = false)
    public PaymentMethod paymentMethod;

    @Column(nullable = false)
    public double amount;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


}
