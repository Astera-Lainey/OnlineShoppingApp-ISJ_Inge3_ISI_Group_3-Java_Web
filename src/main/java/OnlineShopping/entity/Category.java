package OnlineShopping.entity;


import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "categories")
@Data

public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> subcategories;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public enum CategoryStatus {
        FASHION_MEN("Fashion"),
        FASHION_WOMEN("Fashion"),
        FASHION_CHILDREN("Fashion"),
        ACCESSORIES("Fashion"),
        ELECTRONICS("Electronics"),
        HOME_AND_FURNITURE("Home & Furniture"),
        TOYS("Toys"),
        SPORTS("Sports"),
        SHOES("Shoes");

        private final String group;

        CategoryStatus(String group) {
            this.group = group;
        }

        public String getGroup() {
            return group;
        }
    }

}