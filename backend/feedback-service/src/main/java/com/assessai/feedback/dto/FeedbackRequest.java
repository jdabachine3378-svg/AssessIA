package com.assessai.feedback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Score is required")
    private Integer score;

    private String comments;

    private String justification;
}

