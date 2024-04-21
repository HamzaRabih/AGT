package com.example.tachesapp.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/**
 * Classe de configuration pour la mise en place de WebSocket à l'aide de Spring WebSockets.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //WebSocket est un protocole de communication bidirectionnel full-duplex qui permet une communication
    // en temps réel entre un client et un serveur
    // sur une connexion TCP unique.
    // Bien que WebSocket offre une communication bidirectionnelle,
    // il ne spécifie pas le format ou la structure des messages échangés entre le client et le serveur.
    //STOMP (Simple Text Oriented Messaging Protocol) est un protocole de messagerie basé sur des messages
    // qui fournit une couche supplémentaire de communication au-dessus de WebSocket.
    // Il définit un format de message simple et fournit des opérations standard pour la publication,
    // la souscription et la diffusion de messages.
    //Les WebSocket sont souvent utilisés avec STOMP pour plusieurs raisons :

    /**
     * Configure le courtier de messages, activant les points de terminaison du courtier simple.
     * Les messages envoyés à "/topic" et "/queue/" seront acheminés vers les clients.
     * Les messages préfixés par "/app" seront acheminés vers les méthodes de gestion des messages.
     *
     * @param config Configuration du courtier de messages (MessageBrokerRegistry)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue/");//app -> client
        config.setApplicationDestinationPrefixes("/app");//client -> app ,messages envoyés par les clients à l'application.
    }

    /**
     * Enregistre les points de terminaison Stomp, tels que "/ws", pour la communication WebSocket.
     * SockJS est utilisé comme option de repli au cas où WebSocket ne serait pas pris en charge par le client.
     *
     * @param registry Registre des points de terminaison Stomp (StompEndpointRegistry)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
       // Cela signifie que les clients peuvent se connecter à votre application via WebSocket en utilisant l'URL /ws.
        registry.addEndpoint("/ws").withSockJS();
    }
}
