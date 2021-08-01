package edu.scnu.lims.config;

import com.google.common.base.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

import static com.google.common.base.Predicates.not;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @author ZhuangJieYing
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${server.servlet.context-path}")
    private String pathMapping;

    private ApiInfo initApiInfo() {
        Contact contact = new Contact("庄杰颖", "https://github.com/jayying007", "zjy_mc@163.com");
        return new ApiInfo("实验室信息管理平台接口",
                initContextInfo(),
                "1.0.0",
                "服务条款",
                contact,
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html",
                new ArrayList<>()
        );
    }

    private String initContextInfo() {
        return "这里是项目的所有Web API接口";
    }

    @Bean
    public Docket restfulApi() {
        String apiDocUrl = "http://127.0.0.1:8080" + pathMapping + "/swagger-ui.html";
        System.out.println("接口文档地址:" + apiDocUrl);

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("RestfulApi")
//                .genericModelSubstitutes(DeferredResult.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(true)
                .forCodeGeneration(false)
                // base，最终调用接口后会和paths拼接在一起
                .pathMapping(pathMapping)
                .select()
                .paths(doFilteringRules())
                .build()
                .apiInfo(initApiInfo());
    }

    /**
     * 设置过滤规则
     * 这里的过滤规则支持正则匹配
     * @return Predicate
     */
    private Predicate<String> doFilteringRules() {
        // 过滤掉Spring Mvc的统一异常处理Basic Error Controller
        return not(
                regex("/error")
        );
    }
}
