package com.assessai.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateFeedbackRequest {
    
    private String studentId;
    private Integer score;
    private List<String> missingPoints;
    private String studentText;
    private String referenceText;
}
