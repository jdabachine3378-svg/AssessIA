package com.assessai.scoring.repository;

import com.assessai.scoring.model.ScoringRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoringRuleRepository extends JpaRepository<ScoringRule, Long> {
    
    List<ScoringRule> findByExamId(Long examId);
    
    List<ScoringRule> findByQuestionId(Long questionId);
    
    List<ScoringRule> findByIsActiveTrue();
    
    @Query("SELECT sr FROM ScoringRule sr WHERE sr.examId = :examId AND sr.isActive = true")
    List<ScoringRule> findActiveRulesByExamId(@Param("examId") Long examId);
    
    @Query("SELECT sr FROM ScoringRule sr WHERE sr.questionId = :questionId AND sr.isActive = true")
    List<ScoringRule> findActiveRulesByQuestionId(@Param("questionId") Long questionId);
}

