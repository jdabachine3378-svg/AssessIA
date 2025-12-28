package com.assessai.scoring.repository;

import com.assessai.scoring.model.GradingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingResultRepository extends JpaRepository<GradingResult, Long> {
    
    List<GradingResult> findByScoreId(Long scoreId);
    
    List<GradingResult> findByQuestionId(Long questionId);
    
    List<GradingResult> findByScoreIdAndQuestionId(Long scoreId, Long questionId);
}

