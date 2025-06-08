package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    private String name;
    private String description;
    private String brand;
    private Category category;
    private double price;
    private int stockQuantity;

    @ColumnDefault("/assets/img/logo.png")
    private String productImageUrl;

    public Product(int id, String name, String description, String brand, Category category, List<String> images, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.price = price;
//        this.images = images;

    }
}

