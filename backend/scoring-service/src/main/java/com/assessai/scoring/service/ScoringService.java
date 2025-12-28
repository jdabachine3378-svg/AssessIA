package com.assessai.scoring.service;

import com.assessai.scoring.model.*;
import com.assessai.scoring.processor.ScoringProcessor;
import com.assessai.scoring.processor.ScoringProcessorFactory;
import com.assessai.scoring.repository.GradingResultRepository;
import com.assessai.scoring.repository.ScoreRepository;
import com.assessai.scoring.repository.ScoringRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ScoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);
    
    private final ScoreRepository scoreRepository;
    private final GradingResultRepository gradingResultRepository;
    private final ScoringRuleRepository scoringRuleRepository;
    private final ScoringProcessorFactory processorFactory;
    
    @Autowired
    public ScoringService(
            ScoreRepository scoreRepository,
            GradingResultRepository gradingResultRepository,
            ScoringRuleRepository scoringRuleRepository,
            ScoringProcessorFactory processorFactory) {
        this.scoreRepository = scoreRepository;
        this.gradingResultRepository = gradingResultRepository;
        this.scoringRuleRepository = scoringRuleRepository;
        this.processorFactory = processorFactory;
    }
    
    @Transactional
    public Score processScoringRequest(ScoringRequest request) {
        logger.info("Traitement de la demande de scoring pour la copie ID: {}", request.getCopyId());
        
        // Vérifier si un score existe déjà
        Optional<Score> existingScore = scoreRepository.findByCopyId(request.getCopyId());
        if (existingScore.isPresent() && existingScore.get().getStatus() == ScoringStatus.COMPLETED) {
            logger.warn("Un score existe déjà pour la copie ID: {}", request.getCopyId());
            return existingScore.get();
        }
        
        // Sélectionner le processeur approprié
        String algorithmType = request.getScoringAlgorithm();
        if (algorithmType == null || algorithmType.trim().isEmpty()) {
            algorithmType = "AUTOMATIC";
        }
        
        ScoringProcessor processor = processorFactory.getProcessor(algorithmType);
        
        // Traiter le scoring
        Score score = processor.process(request);
        
        // Sauvegarder le score
        score = scoreRepository.save(score);
        
        // Créer les résultats de notation détaillés
        createGradingResults(score, request);
        
        logger.info("Scoring complété pour la copie ID: {} avec un score de {}", 
                request.getCopyId(), score.getTotalScore());
        
        return score;
    }
    
    @Transactional
    public Score createScore(ScoringRequest request) {
        return processScoringRequest(request);
    }
    
    @Transactional(readOnly = true)
    public Optional<Score> getScoreById(Long id) {
        return scoreRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Score> getScoreByCopyId(Long copyId) {
        return scoreRepository.findByCopyId(copyId);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "scores", key = "#examId")
    public List<Score> getScoresByExamId(Long examId) {
        return scoreRepository.findByExamId(examId);
    }
    
    @Transactional(readOnly = true)
    public List<Score> getScoresByStudentId(Long studentId) {
        return scoreRepository.findByStudentId(studentId);
    }
    
    @Transactional(readOnly = true)
    public List<Score> getScoresByExamAndStudent(Long examId, Long studentId) {
        return scoreRepository.findByExamIdAndStudentId(examId, studentId);
    }
    
    @Transactional(readOnly = true)
    public List<Score> getScoresByStatus(ScoringStatus status) {
        return scoreRepository.findByStatus(status);
    }
    
    @Transactional
    public Score updateScore(Long id, Score updatedScore) {
        return scoreRepository.findById(id)
                .map(score -> {
                    if (updatedScore.getTotalScore() != null) {
                        score.setTotalScore(updatedScore.getTotalScore());
                    }
                    if (updatedScore.getStatus() != null) {
                        score.setStatus(updatedScore.getStatus());
                    }
                    if (updatedScore.getGradingDetails() != null) {
                        score.setGradingDetails(updatedScore.getGradingDetails());
                    }
                    return scoreRepository.save(score);
                })
                .orElseThrow(() -> new RuntimeException("Score non trouvé avec l'ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<GradingResult> getGradingResultsByScoreId(Long scoreId) {
        return gradingResultRepository.findByScoreId(scoreId);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "scoringRules", key = "#examId")
    public List<ScoringRule> getScoringRulesByExamId(Long examId) {
        return scoringRuleRepository.findActiveRulesByExamId(examId);
    }
    
    private void createGradingResults(Score score, ScoringRequest request) {
        // Créer un résultat de notation général
        GradingResult result = new GradingResult();
        result.setScoreId(score.getId());
        result.setPointsEarned(score.getTotalScore());
        result.setPointsPossible(score.getMaxScore());
        result.setAutoGraded(true);
        result.setFeedback("Scoring automatique effectué avec l'algorithme: " + score.getScoringAlgorithm());
        
        gradingResultRepository.save(result);
    }
    
    @Transactional(readOnly = true)
    public Long countScoresByExamId(Long examId) {
        return scoreRepository.countByExamId(examId);
    }
    
    @Transactional(readOnly = true)
    public Double getAverageScoreByExamId(Long examId) {
        return scoreRepository.getAverageScoreByExamId(examId);
    }
}

