package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer wishListId;
    @OneToOne(fetch = FetchType.LAZY)
    public Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
