package com.assessai.scoring.processor;

import com.assessai.scoring.model.Score;
import com.assessai.scoring.model.ScoringRequest;
import com.assessai.scoring.model.ScoringStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

@Component
public class AutomaticScoringProcessor implements ScoringProcessor {
    
    private static final String ALGORITHM_TYPE = "AUTOMATIC";
    
    @Override
    public Score process(ScoringRequest request) {
        Score score = new Score();
        score.setCopyId(request.getCopyId());
        score.setExamId(request.getExamId());
        score.setStudentId(request.getStudentId());
        score.setStatus(ScoringStatus.IN_PROGRESS);
        score.setScoringAlgorithm(ALGORITHM_TYPE);
        
        // Algorithme de scoring automatique basique
        BigDecimal calculatedScore = calculateAutomaticScore(request.getContent());
        
        score.setTotalScore(calculatedScore);
        score.setMaxScore(BigDecimal.valueOf(20.0));
        
        // Calcul du pourcentage
        BigDecimal percentage = calculatedScore
                .divide(score.getMaxScore(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        score.setPercentageScore(percentage);
        
        score.setStatus(ScoringStatus.COMPLETED);
        score.setGradingDetails("Scoring automatique effectué avec succès");
        
        return score;
    }
    
    @Override
    public boolean supports(String algorithmType) {
        return ALGORITHM_TYPE.equalsIgnoreCase(algorithmType);
    }
    
    private BigDecimal calculateAutomaticScore(String content) {
        if (content == null || content.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Algorithme simple basé sur la longueur et la structure
        double baseScore = 0.0;
        
        // Points pour la longueur (max 5 points)
        int wordCount = content.trim().split("\\s+").length;
        double lengthScore = Math.min(5.0, wordCount / 10.0);
        
        // Points pour la structure (max 5 points)
        boolean hasParagraphs = content.contains("\n\n") || content.split("\\. ").length > 3;
        double structureScore = hasParagraphs ? 5.0 : 2.5;
        
        // Points pour la ponctuation (max 5 points)
        long punctuationCount = Pattern.compile("[.!?]").matcher(content).results().count();
        double punctuationScore = Math.min(5.0, punctuationCount * 0.5);
        
        // Points pour la diversité lexicale (max 5 points)
        String[] words = content.toLowerCase().split("\\s+");
        long uniqueWords = java.util.Arrays.stream(words).distinct().count();
        double diversityScore = Math.min(5.0, uniqueWords / 5.0);
        
        baseScore = lengthScore + structureScore + punctuationScore + diversityScore;
        
        // Normaliser sur 20
        baseScore = (baseScore / 20.0) * 20.0;
        
        return BigDecimal.valueOf(baseScore).setScale(2, RoundingMode.HALF_UP);
    }
}

