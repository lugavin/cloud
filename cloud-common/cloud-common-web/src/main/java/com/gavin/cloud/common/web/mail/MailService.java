package com.gavin.cloud.common.web.mail;

import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.web.properties.SwaggerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Map;

public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private final SwaggerProperties.Mail mailProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailService(SwaggerProperties appWebProperties,
                       JavaMailSender javaMailSender,
                       MessageSource messageSource,
                       SpringTemplateEngine templateEngine) {
        this.mailProperties = appWebProperties.getMail();
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(MailDTO mailDTO) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, mailDTO.isMultipart(), Constants.CHARSET_UTF_8);
            message.setFrom(mailDTO.getSendFrom());
            message.setTo(mailDTO.getSendTo());
            message.setSubject(mailDTO.getSubject());
            message.setText(mailDTO.getContent(), mailDTO.isHtml());
            javaMailSender.send(mimeMessage);
            LOGGER.debug("Sent email to User '{}'", mailDTO.getSendTo());
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("Email could not be sent to user '{}'", mailDTO.getSendTo(), e);
            } else {
                LOGGER.warn("Email could not be sent to user '{}': {}", mailDTO.getSendTo(), e.getMessage());
            }
        }
    }

    @Async
    public void sendEmailFromTemplate(MailTemplate mailTemplate, String sendTo, Map<String, Object> variables, Locale locale) {
        Context context = new Context(locale);
        context.setVariables(variables);
        String content = templateEngine.process(mailTemplate.getTemplateName(), context);
        String subject = messageSource.getMessage(mailTemplate.getTitleKey(), null, locale);
        sendEmail(new MailDTO(mailProperties.getFrom(), sendTo, subject, content));
    }

}
