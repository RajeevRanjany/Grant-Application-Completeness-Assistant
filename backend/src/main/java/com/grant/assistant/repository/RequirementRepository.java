package com.grant.assistant.repository;

import com.grant.assistant.model.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, String> {

    List<Requirement> findByAssessmentIdOrderByOrderIndexAsc(String assessmentId);
}
