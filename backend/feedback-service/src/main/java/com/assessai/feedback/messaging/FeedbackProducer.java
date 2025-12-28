package com.assessai.feedback.messaging;

import com.assessai.feedback.config.RabbitMQConfig;
import com.assessai.feedback.dto.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendFeedbackCreated(FeedbackResponse feedback) {
        log.info("Sending feedback created event for feedback ID: {}", feedback.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.FEEDBACK_EXCHANGE,
                RabbitMQConfig.FEEDBACK_ROUTING_KEY,
                feedback
        );
        log.info("Feedback event sent successfully");
    }
}


