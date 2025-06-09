package OnlineShopping.entity;

<<<<<<< HEAD
=======
import com.fasterxml.jackson.annotation.JsonBackReference;
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
import OnlineShopping.dto.ImageDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

   @Column
   private String path;
<<<<<<< HEAD
@ManyToOne(fetch = FetchType.LAZY)
=======
@JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
>>>>>>> 51731a978a390f62aafa576272c9877bdff24c38
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
//to build the dto ie convert the image into a dto with id and path
    public ImageDTO toDTO(){
        return ImageDTO.builder()
                .imageId(id)
                .imageUrl(path)
                .productId(product.getId())
                .build();
    }
}
