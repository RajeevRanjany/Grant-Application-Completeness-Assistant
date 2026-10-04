package com.grant.assistant.controller;

import com.grant.assistant.dto.request.ReviewMappingRequest;
import com.grant.assistant.dto.response.MappingResponse;
import com.grant.assistant.exception.ResourceNotFoundException;
import com.grant.assistant.model.RequirementMapping;
import com.grant.assistant.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/mappings")
public class MappingController {

    private final ReviewService reviewService;

    @Autowired
    public MappingController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PatchMapping("/{mappingId}")
    public MappingResponse patchMapping(
            @PathVariable String mappingId,
            @RequestBody ReviewMappingRequest body) {

        RequirementMapping mapping;
        try {
            switch (body.getAction()) {
                case "confirm" -> mapping = reviewService.confirmMapping(mappingId);
                case "correct" -> {
                    if (body.getReviewerNote() == null || body.getReviewerNote().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                                "reviewer_note is required for 'correct' action");
                    }
                    mapping = reviewService.correctMapping(mappingId, body.getReviewerNote());
                }
                case "reject" -> mapping = reviewService.rejectMapping(mappingId, body.getReviewerNote());
                default -> throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Invalid action: " + body.getAction());
            }
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return MappingResponse.builder()
                .id(mapping.getId())
                .requirementId(mapping.getRequirementId())
                .evidenceItemId(mapping.getEvidenceItemId())
                .aiConfidence(mapping.getAiConfidence())
                .aiExplanation(mapping.getAiExplanation())
                .guidelineCitation(mapping.getGuidelineCitation())
                .aiCitation(mapping.getAiCitation())
                .reviewStatus(mapping.getReviewStatus())
                .reviewerNote(mapping.getReviewerNote())
                .reviewedAt(mapping.getReviewedAt())
                .build();
    }
}
