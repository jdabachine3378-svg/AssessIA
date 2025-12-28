package com.assessai.scoring.model;

import java.util.Map;

public class ScoringRequest {
    private Long copyId;
    private Long examId;
    private Long studentId;
    private String content;
    private Map<String, Object> metadata;
    private String scoringAlgorithm;
    private Long correctorId;
    
    public ScoringRequest() {
    }
    
    public ScoringRequest(Long copyId, Long examId, Long studentId, String content) {
        this.copyId = copyId;
        this.examId = examId;
        this.studentId = studentId;
        this.content = content;
    }
    
    // Getters and Setters
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public String getScoringAlgorithm() {
        return scoringAlgorithm;
    }
    
    public void setScoringAlgorithm(String scoringAlgorithm) {
        this.scoringAlgorithm = scoringAlgorithm;
    }
    
    public Long getCorrectorId() {
        return correctorId;
    }
    
    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }
}

