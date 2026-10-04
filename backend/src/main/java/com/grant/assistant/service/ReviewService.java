package com.grant.assistant.service;

import com.grant.assistant.exception.ResourceNotFoundException;
import com.grant.assistant.model.RequirementMapping;
import com.grant.assistant.repository.RequirementMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ReviewService {

    private final RequirementMappingRepository requirementMappingRepository;

    @Autowired
    public ReviewService(RequirementMappingRepository requirementMappingRepository) {
        this.requirementMappingRepository = requirementMappingRepository;
    }

    private RequirementMapping getMappingOrThrow(String mappingId) {
        return requirementMappingRepository.findById(mappingId)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping " + mappingId + " not found"));
    }

    @Transactional
    public RequirementMapping confirmMapping(String mappingId) {
        RequirementMapping mapping = getMappingOrThrow(mappingId);
        if (!"pending".equals(mapping.getReviewStatus())) {
            throw new IllegalStateException(
                    "Cannot confirm mapping with status '" + mapping.getReviewStatus() + "'");
        }
        mapping.setReviewStatus("confirmed");
        mapping.setReviewedAt(Instant.now());
        return requirementMappingRepository.save(mapping);
    }

    @Transactional
    public RequirementMapping correctMapping(String mappingId, String reviewerNote) {
        RequirementMapping mapping = getMappingOrThrow(mappingId);
        if ("rejected".equals(mapping.getReviewStatus())) {
            throw new IllegalStateException("Cannot correct a rejected mapping");
        }
        mapping.setReviewStatus("corrected");
        mapping.setReviewerNote(reviewerNote);
        mapping.setReviewedAt(Instant.now());
        return requirementMappingRepository.save(mapping);
    }

    @Transactional
    public RequirementMapping rejectMapping(String mappingId, String reviewerNote) {
        RequirementMapping mapping = getMappingOrThrow(mappingId);
        mapping.setReviewStatus("rejected");
        mapping.setReviewerNote(reviewerNote);
        mapping.setReviewedAt(Instant.now());
        return requirementMappingRepository.save(mapping);
    }
}
