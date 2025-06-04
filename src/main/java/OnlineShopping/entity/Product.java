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
    @ManyToOne
    @JoinColumn(name = "category_category_id")
    private Category category;
    private double price;
    private int stockQuantity;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;

//    @ElementCollection
//    @CollectionTable(name = "product_colors", joinColumns = @JoinColumn(name = "product_id"))
//    @Column(name = "color")
//    private List<String> colorsAvailable;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

//    @OneToMany(mappedBy = "product")
//    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

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

