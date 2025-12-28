package com.assessai.nlp.service;

import com.assessai.nlp.model.TextAnalysisRequest;
import com.assessai.nlp.model.TextAnalysisResponse;
import com.assessai.nlp.processor.NlpProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NlpService {

    @Autowired
    private NlpProcessor nlpProcessor;

    public TextAnalysisResponse analyzeText(TextAnalysisRequest request) {
        try {
            Map<String, Object> analysis = nlpProcessor.process(request);
            double score = nlpProcessor.calculateScore(request.getText(), analysis);
            
            return new TextAnalysisResponse(
                request.getText(),
                score,
                analysis,
                "SUCCESS",
                "Analyse terminée avec succès"
            );
        } catch (Exception e) {
            return new TextAnalysisResponse(
                request.getText(),
                0.0,
                new HashMap<>(),
                "ERROR",
                "Erreur lors de l'analyse: " + e.getMessage()
            );
        }
    }

    public TextAnalysisResponse analyzeText(String text) {
        TextAnalysisRequest request = new TextAnalysisRequest(text, "fr", null);
        return analyzeText(request);
    }
}

