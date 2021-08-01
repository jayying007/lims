package edu.scnu.lims.config;

import edu.scnu.lims.filter.StatusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * @author ZhuangJieYing
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求
        registry.addInterceptor(statusInterceptor())
                .addPathPatterns("/**");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问/image/**路径时映射到path路径
        String path = System.getProperty("user.dir") + File.separator + "src/main/resources/static/image/";
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + path);
    }

    @Bean
    public StatusInterceptor statusInterceptor() {
        return new StatusInterceptor();
    }
}
