package com.assessai.nlp.processor;

import com.assessai.nlp.model.TextAnalysisRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NlpProcessor {

    public Map<String, Object> process(TextAnalysisRequest request) {
        Map<String, Object> analysis = new HashMap<>();
        
        String text = request.getText();
        if (text == null || text.trim().isEmpty()) {
            return analysis;
        }

        // Analyse de base du texte
        analysis.put("wordCount", countWords(text));
        analysis.put("sentenceCount", countSentences(text));
        analysis.put("characterCount", text.length());
        analysis.put("averageWordLength", calculateAverageWordLength(text));
        analysis.put("language", detectLanguage(text));
        
        // Analyse de qualité
        analysis.put("spellingErrors", detectSpellingErrors(text));
        analysis.put("grammarScore", calculateGrammarScore(text));
        analysis.put("coherenceScore", calculateCoherenceScore(text));
        
        return analysis;
    }

    public double calculateScore(String text, Map<String, Object> analysis) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }

        double score = 100.0;
        
        // Réduction basée sur les erreurs d'orthographe
        Integer spellingErrors = (Integer) analysis.getOrDefault("spellingErrors", 0);
        score -= spellingErrors * 2.0;
        
        // Réduction basée sur le score de grammaire
        Double grammarScore = (Double) analysis.getOrDefault("grammarScore", 100.0);
        score = (score + grammarScore) / 2.0;
        
        // Réduction basée sur la cohérence
        Double coherenceScore = (Double) analysis.getOrDefault("coherenceScore", 100.0);
        score = (score + coherenceScore) / 2.0;
        
        return Math.max(0.0, Math.min(100.0, score));
    }

    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.split("[.!?]+").length;
    }

    private double calculateAverageWordLength(String text) {
        String[] words = text.trim().split("\\s+");
        if (words.length == 0) {
            return 0.0;
        }
        int totalLength = 0;
        for (String word : words) {
            totalLength += word.length();
        }
        return (double) totalLength / words.length;
    }

    private String detectLanguage(String text) {
        // Détection simple basée sur des mots communs
        String lowerText = text.toLowerCase();
        if (lowerText.contains("le ") || lowerText.contains("de ") || lowerText.contains("et ")) {
            return "fr";
        } else if (lowerText.contains("the ") || lowerText.contains("and ") || lowerText.contains("is ")) {
            return "en";
        }
        return "unknown";
    }

    private int detectSpellingErrors(String text) {
        // Simulation simple - à remplacer par un vrai correcteur orthographique
        // Pour l'instant, on retourne 0
        return 0;
    }

    private double calculateGrammarScore(String text) {
        // Simulation simple - à remplacer par un vrai analyseur grammatical
        // Score basé sur la longueur moyenne des phrases
        int sentenceCount = countSentences(text);
        if (sentenceCount == 0) {
            return 0.0;
        }
        int wordCount = countWords(text);
        double avgWordsPerSentence = (double) wordCount / sentenceCount;
        
        // Score optimal entre 10-20 mots par phrase
        if (avgWordsPerSentence >= 10 && avgWordsPerSentence <= 20) {
            return 100.0;
        } else if (avgWordsPerSentence < 5) {
            return 60.0;
        } else if (avgWordsPerSentence > 30) {
            return 70.0;
        }
        return 85.0;
    }

    private double calculateCoherenceScore(String text) {
        // Simulation simple - à remplacer par une vraie analyse de cohérence
        // Basé sur la diversité des mots et la structure
        String[] words = text.trim().split("\\s+");
        if (words.length == 0) {
            return 0.0;
        }
        
        // Calcul de la diversité lexicale
        long uniqueWords = java.util.Arrays.stream(words)
            .map(String::toLowerCase)
            .distinct()
            .count();
        
        double lexicalDiversity = (double) uniqueWords / words.length;
        return lexicalDiversity * 100.0;
    }
}

