package com.assessai.feedback.controller;

import com.assessai.feedback.dto.FeedbackRequest;
import com.assessai.feedback.dto.FeedbackResponse;
import com.assessai.feedback.dto.GenerateFeedbackRequest;
import com.assessai.feedback.model.Feedback;
import com.assessai.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateFeedback(@RequestBody GenerateFeedbackRequest request) {
        // Generate simple feedback based on score
        String feedback = generateFeedbackText(request.getScore(), request.getMissingPoints());
        
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("feedback", feedback);
        
        return ResponseEntity.ok(response);
    }
    
    private String generateFeedbackText(Integer score, java.util.List<String> missingPoints) {
        if (score == null) score = 0;
        
        StringBuilder feedback = new StringBuilder();
        
        if (score >= 16) {
            feedback.append("Excellent travail ! ");
            feedback.append("Vous avez démontré une très bonne compréhension du sujet. ");
        } else if (score >= 12) {
            feedback.append("Bon travail ! ");
            feedback.append("Vous avez une bonne compréhension globale, mais quelques améliorations sont possibles. ");
        } else if (score >= 10) {
            feedback.append("Travail satisfaisant. ");
            feedback.append("Vous avez les bases, mais il reste des points à améliorer. ");
        } else {
            feedback.append("Des efforts supplémentaires sont nécessaires. ");
            feedback.append("Il est important de revoir les concepts clés. ");
        }
        
        if (missingPoints != null && !missingPoints.isEmpty()) {
            feedback.append("\n\nPoints à améliorer : ");
            feedback.append(String.join(", ", missingPoints));
        }
        
        return feedback.toString();
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        FeedbackResponse response = feedbackService.createFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
        FeedbackResponse response = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        List<FeedbackResponse> responses = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByUserId(@PathVariable Long userId) {
        List<FeedbackResponse> responses = feedbackService.getFeedbacksByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByStatus(@PathVariable Feedback.FeedbackStatus status) {
        List<FeedbackResponse> responses = feedbackService.getFeedbacksByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FeedbackResponse> updateFeedbackStatus(
            @PathVariable Long id,
            @RequestParam Feedback.FeedbackStatus status) {
        FeedbackResponse response = feedbackService.updateFeedbackStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}


