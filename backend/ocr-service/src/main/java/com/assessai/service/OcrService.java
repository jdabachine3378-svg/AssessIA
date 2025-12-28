package com.assessai.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Service pour l'extraction de texte à partir d'images (OCR)
 */
@Service
public class OcrService {

    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);
    private final ITesseract tesseract;

    // Supported file types
    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/jpeg"
    );
    private static final List<String> SUPPORTED_IMAGE_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png"
    );
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PDF_EXTENSION = "pdf";

    @Value("${tesseract.data.path:}")
    private String tessDataPath;

    public OcrService() {
        this.tesseract = new Tesseract();
        // Par défaut, Tesseract cherche dans le chemin système
        // Peut être configuré via application.yaml
    }

    /**
     * Validates if the file type is supported
     *
     * @param file File to validate
     * @return true if supported, false otherwise
     */
    public boolean isFileTypeSupported(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        // Check by content type
        if (contentType != null) {
            if (PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
                return true;
            }
            if (SUPPORTED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                return true;
            }
        }

        // Check by file extension as fallback
        if (filename != null) {
            String extension = getFileExtension(filename).toLowerCase();
            if (PDF_EXTENSION.equals(extension)) {
                return true;
            }
            if (SUPPORTED_IMAGE_EXTENSIONS.contains(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets file extension from filename
     *
     * @param filename File name
     * @return File extension without dot
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * Extrait le texte d'une image encodée en Base64
     *
     * @param imageBase64 Image encodée en Base64
     * @param language    Langue pour l'OCR (fra, eng, etc.)
     * @return Texte extrait
     */
    public String extractTextFromBase64(String imageBase64, String language) throws IOException, TesseractException {
        logger.info("Début de l'extraction OCR avec langue: {}", language);
        
        // Décoder l'image Base64
        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        
        if (image == null) {
            throw new IOException("Impossible de décoder l'image Base64");
        }
        
        return extractTextFromImage(image, language);
    }

    /**
     * Extrait le texte d'une image à partir d'une URL
     *
     * @param imageUrl URL de l'image
     * @param language Langue pour l'OCR
     * @return Texte extrait
     */
    public String extractTextFromUrl(String imageUrl, String language) throws IOException, TesseractException {
        logger.info("Début de l'extraction OCR depuis URL: {} avec langue: {}", imageUrl, language);
        
        URL url = URI.create(imageUrl).toURL();
        BufferedImage image = ImageIO.read(url);
        if (image == null) {
            throw new IOException("Impossible de charger l'image depuis l'URL: " + imageUrl);
        }
        
        return extractTextFromImage(image, language);
    }

    /**
     * Extrait le texte d'un MultipartFile (upload)
     * Supports PDF and image files (JPG, PNG)
     *
     * @param file     Fichier à traiter (PDF ou image)
     * @param language Langue pour l'OCR (utilisé uniquement pour les images)
     * @return Texte extrait
     */
    public String extractTextFromFile(MultipartFile file, String language) throws IOException, TesseractException {
        logger.info("Début de l'extraction OCR depuis fichier: {} avec langue: {}", file.getOriginalFilename(), language);
        
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename != null ? filename : "").toLowerCase();
        
        // Check if it's a PDF
        if (PDF_CONTENT_TYPE.equalsIgnoreCase(contentType) || PDF_EXTENSION.equals(extension)) {
            return extractTextFromPdf(file);
        }
        
        // Otherwise, treat as image
        InputStream inputStream = file.getInputStream();
        BufferedImage image = ImageIO.read(inputStream);
        
        if (image == null) {
            throw new IOException("Impossible de lire l'image du fichier: " + file.getOriginalFilename() + 
                ". Format de fichier non supporté ou fichier corrompu.");
        }
        
        return extractTextFromImage(image, language);
    }

    /**
     * Extrait le texte d'un fichier PDF
     *
     * @param file Fichier PDF
     * @return Texte extrait
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        logger.info("Extraction de texte depuis PDF: {}", file.getOriginalFilename());
        
        // Convert InputStream to byte array for PDFBox 3.x
        byte[] pdfBytes = file.getBytes();
        
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            logger.info("Extraction PDF terminée. Longueur du texte: {}", text != null ? text.length() : 0);
            
            return text != null ? text.trim() : "";
        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction du PDF: {}", e.getMessage(), e);
            throw new IOException("Erreur lors de l'extraction du texte du PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Extrait le texte d'un BufferedImage
     *
     * @param image    Image à traiter
     * @param language Langue pour l'OCR
     * @return Texte extrait
     */
    private String extractTextFromImage(BufferedImage image, String language) throws TesseractException {
        // Configurer Tesseract
        if (tessDataPath != null && !tessDataPath.isEmpty()) {
            tesseract.setDatapath(tessDataPath);
        }
        
        // Définir la langue (par défaut: français)
        String lang = (language != null && !language.isEmpty()) ? language : "fra";
        tesseract.setLanguage(lang);
        
        // Configurer les paramètres OCR pour améliorer la qualité
        tesseract.setPageSegMode(1); // Auto-détection de la page
        tesseract.setOcrEngineMode(1); // LSTM OCR Engine
        
        // Effectuer l'extraction
        String extractedText = tesseract.doOCR(image);
        
        logger.info("Extraction OCR terminée. Longueur du texte: {}", extractedText != null ? extractedText.length() : 0);
        
        return extractedText != null ? extractedText.trim() : "";
    }

    /**
     * Calcule le niveau de confiance de l'OCR (approximation)
     * Note: Tesseract ne fournit pas directement un score de confiance global,
     * cette méthode est une approximation basée sur la présence de texte
     *
     * @param extractedText Texte extrait
     * @return Score de confiance entre 0.0 et 1.0
     */
    public double calculateConfidence(String extractedText) {
        if (extractedText == null || extractedText.trim().isEmpty()) {
            return 0.0;
        }
        
        // Approximation: plus le texte est long, plus la confiance est élevée
        // Dans une implémentation réelle, on utiliserait les résultats détaillés de Tesseract
        int length = extractedText.trim().length();
        double confidence = Math.min(1.0, length / 100.0); // Normalisé à 1.0 pour 100 caractères
        
        return Math.max(0.5, confidence); // Minimum 0.5 si du texte est détecté
    }
}

