package com.assessai.feedback.dto;

import com.assessai.feedback.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {

    private Long id;
    private Long userId;
    private Integer score;
    private String comments;
    private String justification;
    private Feedback.FeedbackStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

