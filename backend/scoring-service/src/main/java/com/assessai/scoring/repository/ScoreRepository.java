package com.assessai.scoring.repository;

import com.assessai.scoring.model.Score;
import com.assessai.scoring.model.ScoringStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    
    Optional<Score> findByCopyId(Long copyId);
    
    List<Score> findByExamId(Long examId);
    
    List<Score> findByStudentId(Long studentId);
    
    List<Score> findByExamIdAndStudentId(Long examId, Long studentId);
    
    List<Score> findByStatus(ScoringStatus status);
    
    @Query("SELECT s FROM Score s WHERE s.examId = :examId AND s.status = :status")
    List<Score> findByExamIdAndStatus(@Param("examId") Long examId, @Param("status") ScoringStatus status);
    
    @Query("SELECT COUNT(s) FROM Score s WHERE s.examId = :examId")
    Long countByExamId(@Param("examId") Long examId);
    
    @Query("SELECT AVG(s.totalScore) FROM Score s WHERE s.examId = :examId AND s.status = 'COMPLETED'")
    Double getAverageScoreByExamId(@Param("examId") Long examId);
}

