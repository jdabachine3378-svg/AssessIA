package com.assessai.scoring.controller;

import com.assessai.scoring.model.Score;
import com.assessai.scoring.model.ScoringRequest;
import com.assessai.scoring.model.ScoringStatus;
import com.assessai.scoring.service.ScoringService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/scoring")
@CrossOrigin(origins = "*")
public class ScoringController {
    
    private final ScoringService scoringService;
    
    @Autowired
    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }
    
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@RequestBody Map<String, Object> request) {
        try {
            String studentText = (String) request.get("studentText");
            String referenceText = (String) request.get("referenceText");
            
            if (studentText == null || studentText.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "ERROR");
                error.put("message", "studentText is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (referenceText == null || referenceText.trim().isEmpty()) {
                referenceText = ""; // Optional reference text
            }
            
            // Calculate score based on similarity and content
            double score = calculateScore(studentText, referenceText);
            String feedback = generateFeedback(studentText, referenceText, score);
            
            Map<String, Object> result = new HashMap<>();
            result.put("score", score);
            result.put("maxScore", 20.0);
            result.put("percentage", (score / 20.0) * 100);
            result.put("feedback", feedback);
            result.put("status", "SUCCESS");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Erreur lors de l'évaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    private double calculateScore(String studentText, String referenceText) {
        if (studentText == null || studentText.trim().isEmpty()) {
            return 0.0;
        }
        
        // Base score on text length and quality
        double baseScore = Math.min(20.0, studentText.length() / 10.0);
        
        // If reference text provided, calculate similarity
        if (referenceText != null && !referenceText.trim().isEmpty()) {
            double similarity = calculateSimilarity(studentText.toLowerCase(), referenceText.toLowerCase());
            baseScore = baseScore * 0.5 + (similarity * 20.0 * 0.5);
        }
        
        return Math.round(baseScore * 100.0) / 100.0;
    }
    
    private double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;
        String[] words1 = text1.split("\\s+");
        String[] words2 = text2.split("\\s+");
        
        int commonWords = 0;
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (word1.equals(word2)) {
                    commonWords++;
                    break;
                }
            }
        }
        
        int totalWords = Math.max(words1.length, words2.length);
        return totalWords > 0 ? (double) commonWords / totalWords : 0.0;
    }
    
    private String generateFeedback(String studentText, String referenceText, double score) {
        StringBuilder feedback = new StringBuilder();
        feedback.append("Score: ").append(score).append("/20\n");
        feedback.append("Longueur du texte: ").append(studentText.length()).append(" caractères\n");
        
        if (score < 10) {
            feedback.append("Commentaire: Le texte est trop court ou manque de contenu.");
        } else if (score < 15) {
            feedback.append("Commentaire: Bon effort, mais le contenu peut être amélioré.");
        } else {
            feedback.append("Commentaire: Excellent travail avec un contenu riche.");
        }
        
        return feedback.toString();
    }
    
    @PostMapping("/score")
    public ResponseEntity<Score> createScore(@Valid @RequestBody ScoringRequest request) {
        try {
            Score score = scoringService.createScore(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(score);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/score/{id}")
    public ResponseEntity<Score> getScoreById(@PathVariable Long id) {
        Optional<Score> score = scoringService.getScoreById(id);
        return score.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/score/copy/{copyId}")
    public ResponseEntity<Score> getScoreByCopyId(@PathVariable Long copyId) {
        Optional<Score> score = scoringService.getScoreByCopyId(copyId);
        return score.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/scores/exam/{examId}")
    public ResponseEntity<List<Score>> getScoresByExamId(@PathVariable Long examId) {
        List<Score> scores = scoringService.getScoresByExamId(examId);
        return ResponseEntity.ok(scores);
    }
    
    @GetMapping("/scores/student/{studentId}")
    public ResponseEntity<List<Score>> getScoresByStudentId(@PathVariable Long studentId) {
        List<Score> scores = scoringService.getScoresByStudentId(studentId);
        return ResponseEntity.ok(scores);
    }
    
    @GetMapping("/scores/exam/{examId}/student/{studentId}")
    public ResponseEntity<List<Score>> getScoresByExamAndStudent(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        List<Score> scores = scoringService.getScoresByExamAndStudent(examId, studentId);
        return ResponseEntity.ok(scores);
    }
    
    @GetMapping("/scores/status/{status}")
    public ResponseEntity<List<Score>> getScoresByStatus(@PathVariable ScoringStatus status) {
        List<Score> scores = scoringService.getScoresByStatus(status);
        return ResponseEntity.ok(scores);
    }
    
    @PutMapping("/score/{id}")
    public ResponseEntity<Score> updateScore(
            @PathVariable Long id,
            @RequestBody Score updatedScore) {
        try {
            Score score = scoringService.updateScore(id, updatedScore);
            return ResponseEntity.ok(score);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/score/{id}/results")
    public ResponseEntity<?> getGradingResults(@PathVariable Long id) {
        return scoringService.getScoreById(id)
                .map(score -> {
                    var results = scoringService.getGradingResultsByScoreId(id);
                    return ResponseEntity.ok(results);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/exam/{examId}/statistics")
    public ResponseEntity<Map<String, Object>> getExamStatistics(@PathVariable Long examId) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalScores", scoringService.countScoresByExamId(examId));
        statistics.put("averageScore", scoringService.getAverageScoreByExamId(examId));
        statistics.put("scores", scoringService.getScoresByExamId(examId));
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "scoring-service");
        return ResponseEntity.ok(status);
    }
}

