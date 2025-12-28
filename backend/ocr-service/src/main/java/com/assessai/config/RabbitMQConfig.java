package com.assessai.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ pour le microservice OCR
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queue.ocr.request}")
    private String ocrRequestQueue;

    @Value("${rabbitmq.queue.ocr.response}")
    private String ocrResponseQueue;

    @Value("${rabbitmq.routing.key.ocr.request}")
    private String ocrRequestRoutingKey;

    @Value("${rabbitmq.routing.key.ocr.response}")
    private String ocrResponseRoutingKey;

    /**
     * Créer l'exchange de type topic
     */
    @Bean
    public TopicExchange ocrExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    /**
     * Queue pour recevoir les demandes d'OCR
     */
    @Bean
    public Queue ocrRequestQueue() {
        return QueueBuilder.durable(ocrRequestQueue).build();
    }

    /**
     * Queue pour publier les résultats d'OCR
     */
    @Bean
    public Queue ocrResponseQueue() {
        return QueueBuilder.durable(ocrResponseQueue).build();
    }

    /**
     * Binding entre l'exchange et la queue de requête
     */
    @Bean
    public Binding ocrRequestBinding() {
        return BindingBuilder
                .bind(ocrRequestQueue())
                .to(ocrExchange())
                .with(ocrRequestRoutingKey);
    }

    /**
     * Binding entre l'exchange et la queue de réponse
     */
    @Bean
    public Binding ocrResponseBinding() {
        return BindingBuilder
                .bind(ocrResponseQueue())
                .to(ocrExchange())
                .with(ocrResponseRoutingKey);
    }

    /**
     * Convertisseur JSON pour les messages RabbitMQ
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Template RabbitMQ avec convertisseur JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Factory pour les listeners avec convertisseur JSON
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}





