package com.assessai.scoring.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String SCORING_QUEUE = "scoring.queue";
    public static final String SCORING_EXCHANGE = "scoring.exchange";
    public static final String SCORING_ROUTING_KEY = "scoring.routingkey";
    
    public static final String SCORING_RESULT_QUEUE = "scoring.result.queue";
    public static final String SCORING_RESULT_EXCHANGE = "scoring.result.exchange";
    public static final String SCORING_RESULT_ROUTING_KEY = "scoring.result.routingkey";
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
    
    // Scoring Request Queue
    @Bean
    public Queue scoringQueue() {
        return QueueBuilder.durable(SCORING_QUEUE).build();
    }
    
    @Bean
    public TopicExchange scoringExchange() {
        return new TopicExchange(SCORING_EXCHANGE);
    }
    
    @Bean
    public Binding scoringBinding() {
        return BindingBuilder
                .bind(scoringQueue())
                .to(scoringExchange())
                .with(SCORING_ROUTING_KEY);
    }
    
    // Scoring Result Queue
    @Bean
    public Queue scoringResultQueue() {
        return QueueBuilder.durable(SCORING_RESULT_QUEUE).build();
    }
    
    @Bean
    public TopicExchange scoringResultExchange() {
        return new TopicExchange(SCORING_RESULT_EXCHANGE);
    }
    
    @Bean
    public Binding scoringResultBinding() {
        return BindingBuilder
                .bind(scoringResultQueue())
                .to(scoringResultExchange())
                .with(SCORING_RESULT_ROUTING_KEY);
    }
}

