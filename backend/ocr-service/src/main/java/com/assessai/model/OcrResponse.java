package com.assessai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Modèle pour les réponses d'OCR publiées via RabbitMQ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrResponse implements Serializable {
    private String submissionId;
    private String requestId;
    private String extractedText;
    private double confidence; // Niveau de confiance de l'OCR (0-1)
    private boolean success;
    private String errorMessage;
    private LocalDateTime processedAt;
    private long processingTimeMs; // Temps de traitement en millisecondes
}





