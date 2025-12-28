package com.assessai.service;

import com.assessai.model.OcrResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service pour publier les résultats d'OCR sur RabbitMQ
 */
@Service
public class OcrMessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(OcrMessagePublisher.class);
    
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key.ocr.response}")
    private String routingKey;

    public OcrMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publie un résultat d'OCR sur RabbitMQ
     *
     * @param ocrResponse Réponse OCR à publier
     */
    public void publishOcrResult(OcrResponse ocrResponse) {
        try {
            logger.info("Publication du résultat OCR pour submissionId: {}, requestId: {}", 
                    ocrResponse.getSubmissionId(), ocrResponse.getRequestId());
            
            rabbitTemplate.convertAndSend(exchangeName, routingKey, ocrResponse);
            
            logger.info("Résultat OCR publié avec succès sur l'exchange: {} avec routing key: {}", 
                    exchangeName, routingKey);
        } catch (Exception e) {
            logger.error("Erreur lors de la publication du résultat OCR: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la publication du résultat OCR", e);
        }
    }
}





