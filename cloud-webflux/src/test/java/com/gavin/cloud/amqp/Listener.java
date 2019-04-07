package com.gavin.cloud.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Listener {

    private final AmqpTemplate amqpTemplate;

    public Listener(ObjectProvider<AmqpTemplate> amqpTemplateProvider) {
        this.amqpTemplate = amqpTemplateProvider.getIfAvailable();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "my-queue", durable = "true"),
            exchange = @Exchange(name = "my-exchange", type = ExchangeTypes.TOPIC),
            key = {"#.#"}
    ))
    public void listen(String msg) {
        log.info("====== {} ======", msg);
    }

}
