package OnlineShopping.service;

import OnlineShopping.entity.Product;
import OnlineShopping.entity.ProductImage;
import OnlineShopping.entity.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;


    public ProductImage saveProductImage(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    public List<ProductImage> getImagesByProductId(Integer productId) {
        return productImageRepository.findByProductId(productId);
    }
    public List<ProductImage> getAllImages() {
        return productImageRepository.findAll();
    }

    public void deleteImagesByProductId(Integer productId) {
        productImageRepository.deleteByProductId(productId);
    }

    public void deleteAllImages(List<ProductImage> images){
        productImageRepository.deleteAll(images);
    }


    public void deleteImage(Integer imageId) {
        productImageRepository.deleteById(imageId);
    }

    public void addProductImages(List<MultipartFile> pictures, Product product) {
        for (MultipartFile picture : pictures){

            String fileName = product.getName() + "_Image_" + System.currentTimeMillis();
            String uploadDir = "C:/Users/DELL LATITUDE 7480/IdeaProjects/OnlineShoppingApp-ISJ_Inge3_ISI_Group_3-Java_Web/uploads/products/images/";
            Path uploadPath = Paths.get(uploadDir);

            try {
                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Get file extension from original filename
                String originalFilename = picture.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String fullFileName = fileName + fileExtension;
                Path filePath = uploadPath.resolve(fullFileName);
                Files.write(filePath, picture.getBytes());

//                log.info("Image saved successfully: {}", filePath.toString());

                //creates the corresponding image for each images
                ProductImage productImage = new ProductImage();
                productImage.setPath("uploads/products/images/" + fullFileName);
                productImage.setProduct(product);
                saveProductImage(productImage);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
