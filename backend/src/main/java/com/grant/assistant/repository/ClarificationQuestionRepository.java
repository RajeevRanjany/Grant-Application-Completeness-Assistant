package com.grant.assistant.repository;

import com.grant.assistant.model.ClarificationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClarificationQuestionRepository extends JpaRepository<ClarificationQuestion, String> {

    List<ClarificationQuestion> findByAssessmentId(String assessmentId);
}
