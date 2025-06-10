package OnlineShopping.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Create uploads directory if it doesn't exist
        String uploadPath = "C:/Users/DELL LATITUDE 7480/IdeaProjects/OnlineShoppingApp-ISJ_Inge3_ISI_Group_3-Java_Web/uploads/products/images/";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // Map URL path "/uploads/**" to the file system path
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/Users/DELL LATITUDE 7480/IdeaProjects/OnlineShoppingApp-ISJ_Inge3_ISI_Group_3-Java_Web/uploads/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // Map URL path "/js/**" to the static resources
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // Map URL path "/assets/**" to the static resources
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .mediaType("js", MediaType.valueOf("application/javascript"))
            .mediaType("css", MediaType.valueOf("text/css"))
            .mediaType("html", MediaType.valueOf("text/html"))
            .mediaType("json", MediaType.valueOf("application/json"));
    }
}
