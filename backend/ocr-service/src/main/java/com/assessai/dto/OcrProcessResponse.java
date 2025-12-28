package com.assessai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses REST d'OCR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrProcessResponse {
    private String extractedText;
    private double confidence;
    private boolean success;
    private String errorMessage;
    private LocalDateTime processedAt;
    private long processingTimeMs;
}





