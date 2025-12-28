package com.assessai.nlp.model;

import java.util.List;

public class TextAnalysisRequest {
    private String text;
    private String language;
    private List<String> analysisTypes;

    public TextAnalysisRequest() {
    }

    public TextAnalysisRequest(String text, String language, List<String> analysisTypes) {
        this.text = text;
        this.language = language;
        this.analysisTypes = analysisTypes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getAnalysisTypes() {
        return analysisTypes;
    }

    public void setAnalysisTypes(List<String> analysisTypes) {
        this.analysisTypes = analysisTypes;
    }
}

