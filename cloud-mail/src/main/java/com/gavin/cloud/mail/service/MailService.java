package com.gavin.cloud.mail.service;

import com.gavin.cloud.mail.config.properties.MailProperties;
import com.gavin.cloud.mail.dto.MailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailProperties mailProperties;
    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(MailDTO mailDTO) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, mailDTO.isMultipart(), StandardCharsets.UTF_8.name());
            message.setFrom(mailDTO.getSendFrom());
            message.setTo(mailDTO.getSendTo());
            message.setSubject(mailDTO.getSubject());
            message.setText(mailDTO.getContent(), mailDTO.isHtml());
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", mailDTO.getSendTo());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", mailDTO.getSendTo(), e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", mailDTO.getSendTo(), e.getMessage());
            }
        }
    }

    @Async
    public void sendEmailFromTemplate(MailTemplate mailTemplate, String sendTo, Map<String, Object> variables, Locale locale) {
        Context context = new Context(locale);
        context.setVariables(variables);
        String content = templateEngine.process(mailTemplate.getTemplateName(), context);
        String subject = messageSource.getMessage(mailTemplate.getTitleKey(), new Object[0], locale);
        sendEmail(new MailDTO(mailProperties.getFrom(), sendTo, subject, content));
    }

}
