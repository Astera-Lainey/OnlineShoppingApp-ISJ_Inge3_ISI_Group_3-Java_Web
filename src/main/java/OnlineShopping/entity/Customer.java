package OnlineShopping.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

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

    @OneToMany(mappedBy = "customer")
    private Collection<Review> review;

    public Collection<Review> getReview() {
        return review;
    }

    public void setReview(Collection<Review> review) {
        this.review = review;
    }
}