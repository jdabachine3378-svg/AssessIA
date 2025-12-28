package com.assessai.feedback.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FEEDBACK_QUEUE = "feedback-service.queue";
    public static final String FEEDBACK_EXCHANGE = "feedback-service.exchange";
    public static final String FEEDBACK_ROUTING_KEY = "feedback-service.routing.key";

    @Bean
    public Queue feedbackQueue() {
        return QueueBuilder.durable(FEEDBACK_QUEUE).build();
    }

    @Bean
    public TopicExchange feedbackExchange() {
        return new TopicExchange(FEEDBACK_EXCHANGE);
    }

    @Bean
    public Binding feedbackBinding(Queue feedbackQueue, TopicExchange feedbackExchange) {
        return BindingBuilder
                .bind(feedbackQueue)
                .to(feedbackExchange)
                .with(FEEDBACK_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}

