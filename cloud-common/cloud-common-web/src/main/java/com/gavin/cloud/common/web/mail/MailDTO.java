package com.gavin.cloud.common.web.mail;

import java.io.Serializable;

public class MailDTO implements Serializable {

    private static final long serialVersionUID = 20180501L;

    private final String sendFrom;
    private final String sendTo;
    private final String subject;
    private final String content;
    private final boolean isMultipart;
    private final boolean isHtml;

    public MailDTO(String sendFrom, String sendTo, String subject, String content) {
        this(sendFrom, sendTo, subject, content, false, true);
    }

    public MailDTO(String sendFrom, String sendTo, String subject, String content, boolean isMultipart, boolean isHtml) {
        this.sendFrom = sendFrom;
        this.sendTo = sendTo;
        this.subject = subject;
        this.content = content;
        this.isMultipart = isMultipart;
        this.isHtml = isHtml;
    }

    public String getSendFrom() {
        return sendFrom;
    }

    public String getSendTo() {
        return sendTo;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public boolean isMultipart() {
        return isMultipart;
    }

    public boolean isHtml() {
        return isHtml;
    }

}
