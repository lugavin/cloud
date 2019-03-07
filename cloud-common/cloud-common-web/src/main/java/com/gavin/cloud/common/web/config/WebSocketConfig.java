package com.gavin.cloud.common.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * WebSocketConfig is annotated with @Configuration to indicate that it is a Spring configuration class.
 * It is also annotated @EnableWebSocketMessageBroker. As its name suggests, @EnableWebSocketMessageBroker
 * enables WebSocket message handling, backed by a message broker.
 * <p>
 * The configureMessageBroker() method implements the default method in WebSocketMessageBrokerConfigurer
 * to configure the message broker. It starts by calling enableSimpleBroker() to enable a simple memory-based message broker
 * to carry the greeting messages back to the client on destinations prefixed with "/topic".
 * It also designates the "/app" prefix for messages that are bound for @MessageMapping-annotated methods.
 * This prefix will be used to define all the message mappings; for example, "/app/hello" is the endpoint
 * that the GreetingController.greeting() method is mapped to handle.
 * <p>
 * The registerStompEndpoints() method registers the "/websocket" endpoint, enabling SockJS fallback options
 * so that alternate transports may be used if WebSocket is not available. The SockJS client will attempt to
 * connect to "/websocket" and use the best transport available (websocket, xhr-streaming, xhr-polling, etc).
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

}
