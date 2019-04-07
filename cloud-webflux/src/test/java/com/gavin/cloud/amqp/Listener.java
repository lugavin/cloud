package com.gavin.cloud.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

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
        System.err.println(msg);
        int i = 1 / 0;
    }

}
