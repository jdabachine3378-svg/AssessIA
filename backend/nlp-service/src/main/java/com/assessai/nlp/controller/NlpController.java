package com.assessai.nlp.controller;

import com.assessai.nlp.model.TextAnalysisRequest;
import com.assessai.nlp.model.TextAnalysisResponse;
import com.assessai.nlp.service.NlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nlp")
@CrossOrigin(origins = "*")
public class NlpController {

    @Autowired
    private NlpService nlpService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeText(@RequestBody Map<String, Object> request) {
        try {
            String extractedText = null;
            if (request.containsKey("extractedText")) {
                extractedText = (String) request.get("extractedText");
            } else if (request.containsKey("text")) {
                extractedText = (String) request.get("text");
            }
            
            if (extractedText == null || extractedText.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "ERROR");
                error.put("message", "extractedText is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            TextAnalysisRequest nlpRequest = new TextAnalysisRequest(extractedText, "fr", null);
            TextAnalysisResponse response = nlpService.analyzeText(nlpRequest);
            
            // Return simplified response with keywords and cleanedText
            Map<String, Object> result = new HashMap<>();
            result.put("cleanedText", cleanText(extractedText));
            result.put("keywords", extractKeywords(extractedText));
            result.put("wordCount", response.getAnalysis().get("wordCount"));
            result.put("status", "SUCCESS");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Erreur lors de l'analyse: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    private String cleanText(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ").trim();
    }
    
    private List<String> extractKeywords(String text) {
        if (text == null || text.trim().isEmpty()) return new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");
        List<String> keywords = new ArrayList<>();
        for (String word : words) {
            word = word.replaceAll("[^a-zàâäéèêëïîôùûüÿç]", "");
            if (word.length() > 3) {
                keywords.add(word);
            }
        }
        return keywords.stream().distinct().limit(10).collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/analyze/simple")
    public ResponseEntity<TextAnalysisResponse> analyzeTextSimple(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            if (text == null || text.trim().isEmpty()) {
                TextAnalysisResponse errorResponse = new TextAnalysisResponse();
                errorResponse.setStatus("ERROR");
                errorResponse.setMessage("Le texte ne peut pas être vide");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            TextAnalysisResponse response = nlpService.analyzeText(text);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TextAnalysisResponse errorResponse = new TextAnalysisResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setMessage("Erreur lors de l'analyse: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "nlp-service");
        return ResponseEntity.ok(status);
    }
}

