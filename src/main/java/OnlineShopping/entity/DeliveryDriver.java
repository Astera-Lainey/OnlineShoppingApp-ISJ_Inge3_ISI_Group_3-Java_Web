package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "DeliveryDriver")
@DiscriminatorValue("DeliveryDriver")
public class DeliveryDriver extends User {
    public DeliveryDriver() {
        super();
        this.setRole(Role.DELIVERY);
    }

    private String vehicleInfo;
}
