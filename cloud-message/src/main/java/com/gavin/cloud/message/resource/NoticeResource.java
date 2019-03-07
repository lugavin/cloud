package com.gavin.cloud.message.resource;

import com.gavin.cloud.message.support.MessageProducer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
public class NoticeResource {

    private final MessageProducer messageProducer;

    public NoticeResource(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping("/{message}")
    public void send(@PathVariable String message) {
        messageProducer.send(message);
    }

}
