package com.gavin.cloud.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Recv {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost(ConnectionFactory.DEFAULT_HOST);
        //factory.setPort(ConnectionFactory.DEFAULT_AMQP_PORT);
        //factory.setVirtualHost(ConnectionFactory.DEFAULT_VHOST);
        //factory.setUsername(ConnectionFactory.DEFAULT_USER);
        //factory.setPassword(ConnectionFactory.DEFAULT_PASS);
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            String queueName = "simple_queue";
            channel.queueDeclare(queueName, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println(" [x] Received '" + message + "'");
                    channel.basicAck(envelope.getDeliveryTag(), false);  // 手动ACK
                }
            });
        }


    }
}
