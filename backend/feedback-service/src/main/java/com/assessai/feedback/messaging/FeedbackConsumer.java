package com.assessai.feedback.messaging;

import com.assessai.feedback.config.RabbitMQConfig;
import com.assessai.feedback.dto.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackConsumer {

    @RabbitListener(queues = RabbitMQConfig.FEEDBACK_QUEUE)
    public void receiveFeedback(FeedbackResponse feedback) {
        log.info("Received feedback message on feedback-service queue: {}", feedback);
        // Process the feedback message as needed
    }
}

