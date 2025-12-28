package com.assessai.scoring.consumer;

import com.assessai.scoring.model.ScoringRequest;
import com.assessai.scoring.service.ScoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScoringConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ScoringConsumer.class);
    
    private final ScoringService scoringService;
    
    @Autowired
    public ScoringConsumer(ScoringService scoringService) {
        this.scoringService = scoringService;
    }
    
    @RabbitListener(queues = "${rabbitmq.scoring.queue:scoring.queue}")
    public void consumeScoringRequest(ScoringRequest request) {
        logger.info("Reçu une demande de scoring pour la copie ID: {}", request.getCopyId());
        
        try {
            scoringService.processScoringRequest(request);
            logger.info("Scoring traité avec succès pour la copie ID: {}", request.getCopyId());
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du scoring pour la copie ID: {}", 
                    request.getCopyId(), e);
            // TODO: Implémenter un mécanisme de retry ou de dead letter queue
        }
    }
}

