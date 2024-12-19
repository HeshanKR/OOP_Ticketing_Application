//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker for the WebSocket messaging system.
     * This is used to Set up a simple message broker and an application destination prefix
     * for routing messages.
     *
     * @param config  the {@link MessageBrokerRegistry} used to configure message broker settings.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers WebSocket endpoints that clients will use to connect to the WebSocket server.
     * This is used to define a STOMP endpoint with a fallback SockJS option and configures it to allow
     * cross-origin requests from specified origins.
     *
     * @param registry the {@link StompEndpointRegistry} used to register STOMP endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();
    }
}

