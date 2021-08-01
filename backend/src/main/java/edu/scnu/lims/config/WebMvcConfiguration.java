package edu.scnu.lims.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jay
 * @since 2021/4/13
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(System.getProperty("os.name").startsWith("Linux") ||
                System.getProperty("os.name").startsWith("Mac")) {
            String basePath = "file://" + System.getProperty("user.home");
            registry.addResourceHandler("/image/**").addResourceLocations(basePath + "/lims/images/");
        } else if(System.getProperty("os.name").startsWith("Windows")) {
            String basePath = "file:///" + System.getProperty("user.home");
            registry.addResourceHandler("/image/**").addResourceLocations(basePath + "/lims/images/");
        }
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
