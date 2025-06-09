package OnlineShopping.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("USER")
public class Customer extends User {
    public Customer() {
        super();
        this.setRole(Role.USER);
    }
} 