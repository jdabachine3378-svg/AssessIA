package com.assessai.controller;

import com.assessai.dto.OcrProcessRequest;
import com.assessai.dto.OcrProcessResponse;
import com.assessai.service.OcrService;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Contrôleur REST pour l'API OCR
 */
@RestController
@RequestMapping("/ocr")
@CrossOrigin(origins = "*")
public class OcrController {

    private static final Logger logger = LoggerFactory.getLogger(OcrController.class);

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    /**
     * Endpoint pour traiter une image via upload de fichier (multipart/form-data)
     *
     * @param file     Fichier image à traiter
     * @param language Langue pour l'OCR (optionnel, défaut: fra)
     * @return Réponse avec le texte extrait
     */
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcrProcessResponse> processOcrFromUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false, defaultValue = "fra") String language) {
        
        logger.info("Requête OCR reçue via upload - Fichier: {}, Langue: {}", file.getOriginalFilename(), language);

        long startTime = System.currentTimeMillis();
        OcrProcessResponse response = new OcrProcessResponse();
        response.setProcessedAt(LocalDateTime.now());

        try {
            if (file.isEmpty()) {
                response.setSuccess(false);
                response.setErrorMessage("Le fichier est vide");
                response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file type before processing
            if (!ocrService.isFileTypeSupported(file)) {
                String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
                String fileExtension = filename.contains(".") 
                    ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase() 
                    : "unknown";
                response.setSuccess(false);
                response.setErrorMessage(
                    String.format("Type de fichier non supporté: .%s. Les types supportés sont: PDF, JPG, PNG", 
                        fileExtension)
                );
                response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                logger.warn("Tentative d'upload d'un fichier non supporté: {} (type: {})", 
                    filename, file.getContentType());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String extractedText = ocrService.extractTextFromFile(file, language);
            double confidence = ocrService.calculateConfidence(extractedText);

            response.setExtractedText(extractedText);
            response.setConfidence(confidence);
            response.setSuccess(true);
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            logger.info("OCR traité avec succès - {} caractères extraits", extractedText.length());
            return ResponseEntity.ok(response);

        } catch (TesseractException e) {
            logger.error("Erreur Tesseract: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur OCR: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IOException e) {
            logger.error("Erreur IO: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur de lecture d'image: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Erreur inattendue: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur inattendue: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint pour traiter une image via Base64 (JSON)
     *
     * @param request Requête contenant l'image Base64
     * @return Réponse avec le texte extrait
     */
    @PostMapping(value = "/process/base64", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OcrProcessResponse> processOcrFromBase64(@RequestBody OcrProcessRequest request) {
        logger.info("Requête OCR reçue via Base64 avec langue: {}", request.getLanguage());

        long startTime = System.currentTimeMillis();
        OcrProcessResponse response = new OcrProcessResponse();
        response.setProcessedAt(LocalDateTime.now());

        try {
            String extractedText;
            
            if (request.getImageBase64() != null && !request.getImageBase64().isEmpty()) {
                extractedText = ocrService.extractTextFromBase64(
                        request.getImageBase64(), 
                        request.getLanguage() != null ? request.getLanguage() : "fra"
                );
            } else if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
                extractedText = ocrService.extractTextFromUrl(
                        request.getImageUrl(), 
                        request.getLanguage() != null ? request.getLanguage() : "fra"
                );
            } else {
                response.setSuccess(false);
                response.setErrorMessage("Aucune source d'image fournie (imageBase64 ou imageUrl requis)");
                response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                return ResponseEntity.badRequest().body(response);
            }

            double confidence = ocrService.calculateConfidence(extractedText);

            response.setExtractedText(extractedText);
            response.setConfidence(confidence);
            response.setSuccess(true);
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);

            logger.info("OCR traité avec succès - {} caractères extraits", extractedText.length());
            return ResponseEntity.ok(response);

        } catch (TesseractException e) {
            logger.error("Erreur Tesseract: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur OCR: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IOException e) {
            logger.error("Erreur IO: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur de lecture d'image: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Erreur inattendue: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage("Erreur inattendue: " + e.getMessage());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * Endpoint de santé pour vérifier que le service est opérationnel
     *
     * @return Status du service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OCR Service is running");
    }
}
