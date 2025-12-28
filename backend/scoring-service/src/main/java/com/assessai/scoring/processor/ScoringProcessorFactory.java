package com.assessai.scoring.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoringProcessorFactory {
    
    private final List<ScoringProcessor> processors;
    
    @Autowired
    public ScoringProcessorFactory(List<ScoringProcessor> processors) {
        this.processors = processors;
    }
    
    public ScoringProcessor getProcessor(String algorithmType) {
        final String finalAlgorithmType = (algorithmType == null || algorithmType.trim().isEmpty()) 
                ? "AUTOMATIC" 
                : algorithmType;
        
        return processors.stream()
                .filter(processor -> processor.supports(finalAlgorithmType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun processeur trouvé pour l'algorithme: " + finalAlgorithmType));
    }
}

