package OnlineShopping.entity;

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
@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
//to build the dto ie convert the image into a dto with id and path
    public ImageDTO toDTO(){
        return ImageDTO.builder()
                .imageId(id)
                .imageUrl(path)
                .build();
    }
}
