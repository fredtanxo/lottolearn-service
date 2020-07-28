package xo.fredtan.lottolearn.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableOpenApi
public class SwaggerConfig {
    private static final String VERSION = "0.0.1-SNAPSHOT";

    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user-api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("xo.fredtan.lottolearn"))
                .paths(userPaths())
                .build();
    }

    @Bean
    public Docket courseApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("course-api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("xo.fredtan.lottolearn"))
                .paths(coursePaths())
                .build();
    }

    private Predicate<String> userPaths() {
        return regex("/user.*");
    }

    private Predicate<String> coursePaths() {
        return regex("/course.*");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("「学无止境」网课平台API接口文档")
                .description("LotToLearn online course platform API reference")
                .contact(new Contact("FredTan", "https://github.com/fredtanxo/lottolearn-service", ""))
                .version(VERSION)
                .build();
    }
}
