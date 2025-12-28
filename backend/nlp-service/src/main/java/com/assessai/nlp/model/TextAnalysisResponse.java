package com.assessai.nlp.model;

import java.util.Map;

public class TextAnalysisResponse {
    private String text;
    private double score;
    private Map<String, Object> analysis;
    private String status;
    private String message;

    public TextAnalysisResponse() {
    }

    public TextAnalysisResponse(String text, double score, Map<String, Object> analysis, String status, String message) {
        this.text = text;
        this.score = score;
        this.analysis = analysis;
        this.status = status;
        this.message = message;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Map<String, Object> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Map<String, Object> analysis) {
        this.analysis = analysis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

