package com.gavin.cloud.mail.dto;

import lombok.Data;

import java.io.Serializable;

@Data
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

}
