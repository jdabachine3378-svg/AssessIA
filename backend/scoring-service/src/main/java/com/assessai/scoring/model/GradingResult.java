package com.assessai.scoring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grading_results")
public class GradingResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "score_id", nullable = false)
    private Long scoreId;
    
    @Column(name = "question_id")
    private Long questionId;
    
    @Column(name = "points_earned", precision = 5, scale = 2)
    private BigDecimal pointsEarned;
    
    @Column(name = "points_possible", precision = 5, scale = 2)
    private BigDecimal pointsPossible;
    
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "corrections", columnDefinition = "TEXT")
    private String corrections;
    
    @Column(name = "auto_graded")
    private Boolean autoGraded = false;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    @Column(name = "grading_metadata", columnDefinition = "JSON")
    private String gradingMetadata;
    
    @PrePersist
    protected void onCreate() {
        if (gradedAt == null) {
            gradedAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getScoreId() {
        return scoreId;
    }
    
    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public BigDecimal getPointsEarned() {
        return pointsEarned;
    }
    
    public void setPointsEarned(BigDecimal pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    
    public BigDecimal getPointsPossible() {
        return pointsPossible;
    }
    
    public void setPointsPossible(BigDecimal pointsPossible) {
        this.pointsPossible = pointsPossible;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public String getCorrections() {
        return corrections;
    }
    
    public void setCorrections(String corrections) {
        this.corrections = corrections;
    }
    
    public Boolean getAutoGraded() {
        return autoGraded;
    }
    
    public void setAutoGraded(Boolean autoGraded) {
        this.autoGraded = autoGraded;
    }
    
    public LocalDateTime getGradedAt() {
        return gradedAt;
    }
    
    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }
    
    public String getGradingMetadata() {
        return gradingMetadata;
    }
    
    public void setGradingMetadata(String gradingMetadata) {
        this.gradingMetadata = gradingMetadata;
    }
}

