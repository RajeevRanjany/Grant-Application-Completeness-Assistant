package com.grant.assistant.service;

import com.grant.assistant.exception.BadRequestException;
import com.grant.assistant.exception.ResourceNotFoundException;
import com.grant.assistant.model.SupportingDocument;
import com.grant.assistant.repository.AssessmentRepository;
import com.grant.assistant.repository.SupportingDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SupportingDocumentService {

    private final SupportingDocumentRepository repository;
    private final AssessmentRepository assessmentRepository;

    @Autowired
    public SupportingDocumentService(
            SupportingDocumentRepository repository,
            AssessmentRepository assessmentRepository) {
        this.repository = repository;
        this.assessmentRepository = assessmentRepository;
    }

    @Transactional
    public SupportingDocument create(String assessmentId, String name, String description) {
        if (!assessmentRepository.existsById(assessmentId)) {
            throw new ResourceNotFoundException("Assessment " + assessmentId + " not found");
        }
        if (name == null || name.isBlank()) {
            throw new BadRequestException("name is required");
        }
        SupportingDocument doc = new SupportingDocument();
        doc.setId(UUID.randomUUID().toString().replace("-", ""));
        doc.setAssessmentId(assessmentId);
        doc.setName(name.strip());
        doc.setDescription(description);
        doc.setReceived(false);
        return repository.save(doc);
    }

    @Transactional(readOnly = true)
    public List<SupportingDocument> listForAssessment(String assessmentId) {
        if (!assessmentRepository.existsById(assessmentId)) {
            throw new ResourceNotFoundException("Assessment " + assessmentId + " not found");
        }
        return repository.findByAssessmentIdOrderByCreatedAtAsc(assessmentId);
    }

    @Transactional
    public SupportingDocument update(String docId, String name, String description, Boolean received, String notes) {
        SupportingDocument doc = repository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Supporting document " + docId + " not found"));

        if (name != null) {
            if (name.isBlank()) throw new BadRequestException("name cannot be blank");
            doc.setName(name.strip());
        }
        if (description != null) {
            doc.setDescription(description);
        }
        if (notes != null) {
            doc.setNotes(notes);
        }
        if (received != null && received != doc.isReceived()) {
            doc.setReceived(received);
            doc.setReceivedAt(received ? Instant.now() : null);
        }
        return repository.save(doc);
    }

    @Transactional
    public void delete(String docId) {
        if (!repository.existsById(docId)) {
            throw new ResourceNotFoundException("Supporting document " + docId + " not found");
        }
        repository.deleteById(docId);
    }
}
