package OnlineShopping.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/products/images/**")
                .addResourceLocations("file:///C:/Users/HP/Desktop/OnlineShoppingApp-ISJ_Inge3_ISI_Group_3-Java_Web/uploads/products/images/");
    }

}
