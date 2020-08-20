package com.gavin.cloud.mail.config;

import com.gavin.cloud.common.base.util.Constants;
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

@Configuration
public class MailConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.mail", ignoreUnknownFields = false)
    public MailProperties mailProperties() {
        return new MailProperties();
    }

    @Bean
    public MailService mailService(JavaMailSender javaMailSender,
                                   MessageSource messageSource,
                                   SpringTemplateEngine templateEngine) {
        return new MailService(mailProperties(), javaMailSender, messageSource, templateEngine);
    }

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5 emails")
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("mails/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding(Constants.CHARSET_UTF_8);
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }

}
