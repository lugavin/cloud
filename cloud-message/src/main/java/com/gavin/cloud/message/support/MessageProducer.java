package com.gavin.cloud.message.support;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(Source.class)
public class MessageProducer {

    private final Source source;

    public MessageProducer(Source source) {
        this.source = source;
    }

    public void send(String message) {
        source.output().send(MessageBuilder.withPayload(message).build());
    }

}
