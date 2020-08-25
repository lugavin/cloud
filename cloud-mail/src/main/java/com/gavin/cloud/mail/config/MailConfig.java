package com.gavin.cloud.mail.config;

import com.gavin.cloud.mail.config.properties.MailProperties;
import com.gavin.cloud.mail.service.MailService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
class MailConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.mail", ignoreUnknownFields = false)
    MailProperties mailProperties() {
        return new MailProperties();
    }

    @Bean
    MailService mailService(JavaMailSender javaMailSender,
                            MessageSource messageSource,
                            SpringTemplateEngine templateEngine) {
        return new MailService(mailProperties(), javaMailSender, messageSource, templateEngine);
    }

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5 emails")
    ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("mails/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }

}
