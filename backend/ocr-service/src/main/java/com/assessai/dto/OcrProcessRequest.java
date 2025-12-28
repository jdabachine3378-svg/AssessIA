package com.assessai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les requêtes REST d'OCR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrProcessRequest {
    private String imageBase64;
    private String imageUrl;
    private String language = "fra"; // Français par défaut
}





