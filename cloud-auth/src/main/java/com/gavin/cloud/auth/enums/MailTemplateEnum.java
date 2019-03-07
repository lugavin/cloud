package com.gavin.cloud.auth.enums;

import com.gavin.cloud.common.web.mail.MailTemplate;

public enum MailTemplateEnum implements MailTemplate {

    ACCOUNT_CREATION("creationEmail", "email.activation.title"),
    ACCOUNT_ACTIVATION("activationEmail", "email.activation.title"),
    PASSWORD_RESET("passwordResetEmail", "email.reset.title");

    private final String templateName;
    private final String titleKey;

    MailTemplateEnum(String templateName, String titleKey) {
        this.templateName = templateName;
        this.titleKey = titleKey;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public String getTitleKey() {
        return titleKey;
    }

}
