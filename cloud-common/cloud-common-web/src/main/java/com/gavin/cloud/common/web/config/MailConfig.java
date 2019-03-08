package com.gavin.cloud.common.web.config;

import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.web.mail.MailService;
import com.gavin.cloud.common.web.properties.SwaggerProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class MailConfig {

    @Bean
    public MailService mailService(SwaggerProperties appWebProperties,
                                   JavaMailSender javaMailSender,
                                   MessageSource messageSource,
                                   SpringTemplateEngine templateEngine) {
        return new MailService(appWebProperties, javaMailSender, messageSource, templateEngine);
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
