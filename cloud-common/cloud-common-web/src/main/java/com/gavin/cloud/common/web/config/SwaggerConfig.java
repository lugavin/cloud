package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.util.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

import static springfox.documentation.builders.PathSelectors.ant;

/**
 * @see springfox.documentation.swagger2.web.Swagger2Controller
 */
@Slf4j
@Configuration
@EnableSwagger2
@Profile(Constants.PROFILE_SWAGGER)
public class SwaggerConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.swagger", ignoreUnknownFields = false)
    public SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
    }

    /**
     * Springfox configuration for the API Swagger docs.
     *
     * @return the Swagger Springfox configuration
     */
    @Bean
    public Docket swaggerSpringfoxApiDocket() {
        log.debug("Starting Swagger");
        StopWatch watch = new StopWatch();
        watch.start();

        SwaggerProperties swaggerProperties = swaggerProperties();
        Contact contact = new Contact(
                swaggerProperties.getContactName(),
                swaggerProperties.getContactUrl(),
                swaggerProperties.getContactEmail()
        );

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .contact(contact)
                .version(swaggerProperties.getVersion())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .host(swaggerProperties.getHost())
                .protocols(new HashSet<>(Arrays.asList(swaggerProperties.getProtocols())))
                .apiInfo(apiInfo)
                .forCodeGeneration(true)
                .directModelSubstitute(ByteBuffer.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .select()
                .paths(ant(swaggerProperties.getDefaultIncludePattern()))
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .build();
        watch.stop();
        log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
        return docket;
    }

    @Data
    private static class SwaggerProperties {

        private String title;
        private String description;
        private String version;
        private String termsOfServiceUrl;
        private String contactName;
        private String contactUrl;
        private String contactEmail;
        private String license;
        private String licenseUrl;
        private String basePackage;
        private String defaultIncludePattern;
        private String host;
        private String[] protocols;

    }

}
