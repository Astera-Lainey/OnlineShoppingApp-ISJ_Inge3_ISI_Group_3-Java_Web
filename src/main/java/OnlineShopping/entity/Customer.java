package OnlineShopping.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
@DiscriminatorValue("USER")
public class Customer extends User {
    public Customer() {
        super();
        this.setRole(Role.USER);
    }
} 