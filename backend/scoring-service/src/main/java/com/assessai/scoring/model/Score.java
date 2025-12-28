package com.assessai.scoring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "copy_id", nullable = false)
    private Long copyId;
    
    @NotNull
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @NotNull
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "20.0")
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;
    
    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore = BigDecimal.valueOf(20.0);
    
    @Column(name = "percentage_score", precision = 5, scale = 2)
    private BigDecimal percentageScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ScoringStatus status = ScoringStatus.PENDING;
    
    @Column(name = "grading_details", columnDefinition = "TEXT")
    private String gradingDetails;
    
    @Column(name = "corrector_id")
    private Long correctorId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "scoring_algorithm")
    private String scoringAlgorithm;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCopyId() {
        return copyId;
    }
    
    public void setCopyId(Long copyId) {
        this.copyId = copyId;
    }
    
    public Long getExamId() {
        return examId;
    }
    
    public void setExamId(Long examId) {
        this.examId = examId;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public BigDecimal getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }
    
    public BigDecimal getMaxScore() {
        return maxScore;
    }
    
    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }
    
    public BigDecimal getPercentageScore() {
        return percentageScore;
    }
    
    public void setPercentageScore(BigDecimal percentageScore) {
        this.percentageScore = percentageScore;
    }
    
    public ScoringStatus getStatus() {
        return status;
    }
    
    public void setStatus(ScoringStatus status) {
        this.status = status;
    }
    
    public String getGradingDetails() {
        return gradingDetails;
    }
    
    public void setGradingDetails(String gradingDetails) {
        this.gradingDetails = gradingDetails;
    }
    
    public Long getCorrectorId() {
        return correctorId;
    }
    
    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getScoringAlgorithm() {
        return scoringAlgorithm;
    }
    
    public void setScoringAlgorithm(String scoringAlgorithm) {
        this.scoringAlgorithm = scoringAlgorithm;
    }
}

