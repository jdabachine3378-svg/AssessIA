package com.assessai.listener;

import com.assessai.model.OcrRequest;
import com.assessai.model.OcrResponse;
import com.assessai.service.OcrMessagePublisher;
import com.assessai.service.OcrService;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Listener RabbitMQ pour recevoir les demandes d'OCR
 */
@Component
public class OcrRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(OcrRequestListener.class);

    private final OcrService ocrService;
    private final OcrMessagePublisher messagePublisher;

    public OcrRequestListener(OcrService ocrService, OcrMessagePublisher messagePublisher) {
        this.ocrService = ocrService;
        this.messagePublisher = messagePublisher;
    }

    @Value("${rabbitmq.queue.ocr.request}")
    private String queueName;

    /**
     * Écoute les messages d'OCR dans la queue
     *
     * @param ocrRequest Requête d'OCR reçue
     */
    @RabbitListener(queues = "${rabbitmq.queue.ocr.request}")
    public void handleOcrRequest(OcrRequest ocrRequest) {
        logger.info("Réception d'une demande d'OCR - SubmissionId: {}, RequestId: {}", 
                ocrRequest.getSubmissionId(), ocrRequest.getRequestId());

        long startTime = System.currentTimeMillis();
        OcrResponse response = new OcrResponse();
        response.setSubmissionId(ocrRequest.getSubmissionId());
        response.setRequestId(ocrRequest.getRequestId());
        response.setProcessedAt(LocalDateTime.now());

        try {
            String extractedText;
            String language = ocrRequest.getLanguage() != null ? ocrRequest.getLanguage() : "fra";

            // Traiter l'image selon la source (Base64 ou URL)
            if (ocrRequest.getImageBase64() != null && !ocrRequest.getImageBase64().isEmpty()) {
                logger.debug("Traitement d'une image Base64");
                extractedText = ocrService.extractTextFromBase64(ocrRequest.getImageBase64(), language);
            } else if (ocrRequest.getImageUrl() != null && !ocrRequest.getImageUrl().isEmpty()) {
                logger.debug("Traitement d'une image depuis URL: {}", ocrRequest.getImageUrl());
                extractedText = ocrService.extractTextFromUrl(ocrRequest.getImageUrl(), language);
            } else {
                throw new IllegalArgumentException("Aucune source d'image fournie (Base64 ou URL)");
            }

            // Calculer la confiance
            double confidence = ocrService.calculateConfidence(extractedText);

            // Construire la réponse de succès
            response.setExtractedText(extractedText);
            response.setConfidence(confidence);
            response.setSuccess(true);
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            logger.info("OCR traité avec succès - Texte extrait ({} caractères), Confiance: {}", 
                    extractedText.length(), confidence);

        } catch (TesseractException e) {
            logger.error("Erreur Tesseract lors du traitement OCR: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur OCR: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        } catch (IOException e) {
            logger.error("Erreur IO lors du traitement OCR: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur de lecture d'image: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du traitement OCR: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur inattendue: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        }

        // Publier la réponse sur RabbitMQ
        try {
            messagePublisher.publishOcrResult(response);
            logger.info("Réponse OCR publiée avec succès pour RequestId: {}", ocrRequest.getRequestId());
        } catch (Exception e) {
            logger.error("Erreur lors de la publication de la réponse OCR: {}", e.getMessage(), e);
        }
    }
}





