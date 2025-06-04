package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users_dashboards")
public class UsersDashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer Customer;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private DeliveryDriver deliveryDriver;


} 