package com.assessai.scoring.processor;

import com.assessai.scoring.model.Score;
import com.assessai.scoring.model.ScoringRequest;
import com.assessai.scoring.model.ScoringStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class KeywordBasedScoringProcessor implements ScoringProcessor {
    
    private static final String ALGORITHM_TYPE = "KEYWORD_BASED";
    
    @Override
    public Score process(ScoringRequest request) {
        Score score = new Score();
        score.setCopyId(request.getCopyId());
        score.setExamId(request.getExamId());
        score.setStudentId(request.getStudentId());
        score.setStatus(ScoringStatus.IN_PROGRESS);
        score.setScoringAlgorithm(ALGORITHM_TYPE);
        
        BigDecimal calculatedScore = calculateKeywordScore(request.getContent(), request.getMetadata());
        
        score.setTotalScore(calculatedScore);
        score.setMaxScore(BigDecimal.valueOf(20.0));
        
        BigDecimal percentage = calculatedScore
                .divide(score.getMaxScore(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        score.setPercentageScore(percentage);
        
        score.setStatus(ScoringStatus.COMPLETED);
        score.setGradingDetails("Scoring basé sur les mots-clés effectué");
        
        return score;
    }
    
    @Override
    public boolean supports(String algorithmType) {
        return ALGORITHM_TYPE.equalsIgnoreCase(algorithmType);
    }
    
    @SuppressWarnings("unchecked")
    private BigDecimal calculateKeywordScore(String content, Map<String, Object> metadata) {
        if (content == null || content.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        String contentLower = content.toLowerCase();
        double score = 0.0;
        double maxScore = 20.0;
        
        // Récupérer les mots-clés attendus depuis les métadonnées
        List<String> expectedKeywords = new ArrayList<>();
        if (metadata != null && metadata.containsKey("keywords")) {
            Object keywordsObj = metadata.get("keywords");
            if (keywordsObj instanceof List) {
                expectedKeywords = (List<String>) keywordsObj;
            }
        }
        
        // Si pas de mots-clés fournis, utiliser des mots-clés par défaut
        if (expectedKeywords.isEmpty()) {
            expectedKeywords = Arrays.asList("analyse", "exemple", "conclusion", "développement", "argument");
        }
        
        // Calculer le score basé sur la présence des mots-clés
        double pointsPerKeyword = maxScore / expectedKeywords.size();
        int foundKeywords = 0;
        
        for (String keyword : expectedKeywords) {
            if (contentLower.contains(keyword.toLowerCase())) {
                score += pointsPerKeyword;
                foundKeywords++;
            }
        }
        
        // Bonus pour la cohérence et la structure
        if (foundKeywords > 0) {
            double bonus = (foundKeywords / (double) expectedKeywords.size()) * 5.0;
            score += bonus;
        }
        
        // Limiter à 20 points max
        score = Math.min(score, maxScore);
        
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }
}

