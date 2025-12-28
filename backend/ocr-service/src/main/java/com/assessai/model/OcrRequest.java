package com.assessai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Modèle pour les demandes d'OCR reçues via RabbitMQ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrRequest implements Serializable {
    private String submissionId;
    private String imageBase64; // Image encodée en Base64
    private String imageUrl; // Ou URL de l'image
    private String language; // Langue pour l'OCR (fra, eng, etc.)
    private String requestId; // ID unique de la requête
}





