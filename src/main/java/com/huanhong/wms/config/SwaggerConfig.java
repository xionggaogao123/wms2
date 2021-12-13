package com.huanhong.wms.config;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.List;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean(value = "default")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("1.X版本")
                .select()
                // 指定controller存放的目录路径
                .apis(RequestHandlerSelectors.basePackage("com.huanhong.wms.controller"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(CollectionUtil.newArrayList(securityContext()))
                .securitySchemes(securitySchemes());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("仓库管理系统")
                .description("充分运用大数据、人工智能、物联网等新技术，建设以大数据智能应用为核心的“仓库管理系统”")
                .termsOfServiceUrl("https://www.aiairy.com/wms/api")
                .version("开发版")
//                .contact(new Contact(
//                        "Deyi.Liu",
//                        "https://liudeyi.cn",
//                        "root@liudeyi.cn"
//                ))
                .build();
    }

    private List<ApiKey> securitySchemes() {
        return CollectionUtil.newArrayList(
                new ApiKey("Authorization", "Authorization", "header"));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return CollectionUtil.newArrayList(new SecurityReference("Authorization", authorizationScopes));
    }
}

