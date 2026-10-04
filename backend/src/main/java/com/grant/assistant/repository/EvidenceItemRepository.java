package com.grant.assistant.repository;

import com.grant.assistant.model.EvidenceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceItemRepository extends JpaRepository<EvidenceItem, String> {

    List<EvidenceItem> findByAssessmentIdOrderByOrderIndexAsc(String assessmentId);
}
