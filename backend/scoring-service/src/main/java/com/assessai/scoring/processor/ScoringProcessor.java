package com.assessai.scoring.processor;

import com.assessai.scoring.model.Score;
import com.assessai.scoring.model.ScoringRequest;

public interface ScoringProcessor {
    
    /**
     * Traite une demande de scoring
     * @param request La demande de scoring
     * @return Le score calculé
     */
    Score process(ScoringRequest request);
    
    /**
     * Vérifie si ce processeur peut traiter le type de scoring demandé
     * @param algorithmType Le type d'algorithme
     * @return true si ce processeur peut traiter ce type
     */
    boolean supports(String algorithmType);
}

