package com.assessai.feedback.service;

import com.assessai.feedback.dto.FeedbackRequest;
import com.assessai.feedback.dto.FeedbackResponse;
import com.assessai.feedback.messaging.FeedbackProducer;
import com.assessai.feedback.model.Feedback;
import com.assessai.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackProducer feedbackProducer;

    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        log.info("Creating feedback for user: {}", request.getUserId());

        Feedback feedback = Feedback.builder()
                .userId(request.getUserId())
                .score(request.getScore())
                .comments(request.getComments())
                .justification(request.getJustification())
                .status(Feedback.FeedbackStatus.PENDING)
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback created with ID: {}", savedFeedback.getId());

        FeedbackResponse response = mapToResponse(savedFeedback);
        // Send message to RabbitMQ
        feedbackProducer.sendFeedbackCreated(response);

        return response;
    }

    public FeedbackResponse getFeedbackById(Long id) {
        log.info("Fetching feedback with ID: {}", id);
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        return mapToResponse(feedback);
    }

    public List<FeedbackResponse> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        return feedbackRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FeedbackResponse> getFeedbacksByUserId(Long userId) {
        log.info("Fetching feedbacks for user: {}", userId);
        return feedbackRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FeedbackResponse> getFeedbacksByStatus(Feedback.FeedbackStatus status) {
        log.info("Fetching feedbacks with status: {}", status);
        return feedbackRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponse updateFeedbackStatus(Long id, Feedback.FeedbackStatus status) {
        log.info("Updating feedback {} status to {}", id, status);
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        feedback.setStatus(status);
        Feedback updatedFeedback = feedbackRepository.save(feedback);

        return mapToResponse(updatedFeedback);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        log.info("Deleting feedback with ID: {}", id);
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found with id: " + id);
        }
        feedbackRepository.deleteById(id);
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .userId(feedback.getUserId())
                .score(feedback.getScore())
                .comments(feedback.getComments())
                .justification(feedback.getJustification())
                .status(feedback.getStatus())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }
}

