package OnlineShopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    public Integer transactionId;
    public Status status;
    public PaymentMethod paymentMethod;
    public Date paymentDate;
    public double amount;
}
