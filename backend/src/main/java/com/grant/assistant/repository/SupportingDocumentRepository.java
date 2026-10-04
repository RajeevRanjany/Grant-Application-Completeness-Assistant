package com.grant.assistant.repository;

import com.grant.assistant.model.SupportingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportingDocumentRepository extends JpaRepository<SupportingDocument, String> {
    List<SupportingDocument> findByAssessmentIdOrderByCreatedAtAsc(String assessmentId);
}
