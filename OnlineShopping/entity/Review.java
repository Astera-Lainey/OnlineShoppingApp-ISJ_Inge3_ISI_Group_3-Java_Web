package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;
    public String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    public Customer customer;
    public int rating;
    public LocalDate reviewDate;
}
