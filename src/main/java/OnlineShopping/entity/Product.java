package OnlineShopping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private ClothesSize clothesSize;
    private ShoeSize shoeSize;
    private String color;
    private int stockQuantity;

    // Constructor without images (as it was commented out)
    public Product(int id, String name, String description, String brand, Category category, double price, ClothesSize clothesSize, ShoeSize shoeSize, String color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.clothesSize = clothesSize;
        this.shoeSize = shoeSize;
        this.color = color;
    }

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>(); // Collection of reviews for this product
}