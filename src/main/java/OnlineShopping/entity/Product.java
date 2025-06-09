package OnlineShopping.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private int initialStockQuantity;

    @PrePersist
    protected void onCreate() {
        if (initialStockQuantity == 0) {
            initialStockQuantity = stockQuantity;
        }
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductImage> images = new ArrayList<>();

    public Product(int id, String name, String description, String brand, Category category, List<String> images, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.price = price;
//        this.images = images;

    }

    // Helper method to add an image
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    // Helper method to remove an image
    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }
}

