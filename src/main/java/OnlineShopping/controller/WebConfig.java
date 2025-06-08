package OnlineShopping.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path "/uploads/**" to the file system path
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/Users/DELL LATITUDE 7480/IdeaProjects/OnlineShoppingApp-ISJ_Inge3_ISI_Group_3-Java_Web/uploads/products/images/");
    }
}
