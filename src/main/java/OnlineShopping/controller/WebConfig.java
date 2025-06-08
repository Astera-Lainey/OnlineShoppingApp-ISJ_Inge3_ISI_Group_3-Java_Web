package OnlineShopping.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path "/uploads/**" to the file system path
        registry.addResourceHandler("/uploads/products/images/**")
                .addResourceLocations("file:///C:\\Users\\DELL LATITUDE 7480\\IdeaProjects\\OnlineShoppingApp-ISJ_Inge3_ISI_Group_3-Java_Web\\uploads\\products\\images\\");

        // Map URL path "/js/**" to the static resources
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // Map URL path "/assets/**" to the static resources
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
    }
}
