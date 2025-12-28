package com.assessai.feedback.repository;

import com.assessai.feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUserId(Long userId);

    List<Feedback> findByStatus(Feedback.FeedbackStatus status);

    Optional<Feedback> findByIdAndUserId(Long id, Long userId);
}

