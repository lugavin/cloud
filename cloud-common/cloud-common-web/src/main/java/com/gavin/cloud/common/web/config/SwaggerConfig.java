package com.gavin.cloud.common.web.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

import static springfox.documentation.builders.PathSelectors.ant;

/**
 * @see springfox.documentation.swagger2.web.Swagger2ControllerWebMvc
 * @see springfox.documentation.swagger2.web.Swagger2ControllerWebFlux
 */
@Slf4j
@EnableOpenApi
@Configuration
class SwaggerConfig {

    /**
     * Springfox configuration for the API Swagger docs.
     *
     * @return the Swagger Springfox configuration
     */
    @Bean
    Docket docket() {
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

        Docket docket = new Docket(DocumentationType.OAS_30)
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

    @Bean
    @ConfigurationProperties("app.swagger")
    SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
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
