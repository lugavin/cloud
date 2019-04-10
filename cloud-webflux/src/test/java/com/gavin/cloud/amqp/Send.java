package com.gavin.cloud.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * @see <a href="https://github.com/rabbitmq/rabbitmq-tutorials">rabbitmq-tutorials</a>
 */
public class Send {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost(ConnectionFactory.DEFAULT_HOST);
        //factory.setPort(ConnectionFactory.DEFAULT_AMQP_PORT);
        //factory.setVirtualHost(ConnectionFactory.DEFAULT_VHOST);
        //factory.setUsername(ConnectionFactory.DEFAULT_USER);
        //factory.setPassword(ConnectionFactory.DEFAULT_PASS);
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            String queueName = "simple_queue";
            channel.queueDeclare(queueName, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
