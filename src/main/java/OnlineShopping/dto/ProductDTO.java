package OnlineShopping.dto;

import OnlineShopping.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ProductDTO {
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String brand;

    @NotNull
    private Category category;

    @NotNull
    private double price;

    @NotNull
    private int stock;

    public @NotNull List<MultipartFile> getImage() {
        return images;
    }

    public void setImage(@NotNull List<MultipartFile> image) {
        this.images = image;
    }

    @NotNull
    private List<MultipartFile> images;

    public @NotNull double getPrice() {
        return price;
    }

    public void setPrice(@NotNull double price) {
        this.price = price;
    }

    public @NotNull int getStock() {
        return stock;
    }

    public void setStock(@NotNull int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotBlank String getBrand() {
        return brand;
    }

    public void setBrand(@NotBlank String brand) {
        this.brand = brand;
    }

    public @NotNull Category getCategory() {
        return category;
    }

    public void setCategory(@NotNull Category category) {
        this.category = category;
    }

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }
}

