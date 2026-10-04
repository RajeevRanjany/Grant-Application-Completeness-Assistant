package com.grant.assistant.controller;

import com.grant.assistant.dto.request.CreateAssessmentRequest;
import com.grant.assistant.dto.response.*;
import com.grant.assistant.exception.ResourceNotFoundException;
import com.grant.assistant.model.*;
import com.grant.assistant.repository.*;
import com.grant.assistant.service.AssessmentService;
import com.grant.assistant.service.SummaryService;
import com.grant.assistant.service.ai.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final SummaryService summaryService;
    private final AIService aiService;
    private final RequirementRepository requirementRepository;
    private final RequirementMappingRepository requirementMappingRepository;
    private final ClarificationQuestionRepository clarificationQuestionRepository;
    private final UnsupportedClaimRepository unsupportedClaimRepository;

    @Autowired
    public AssessmentController(
            AssessmentService assessmentService,
            SummaryService summaryService,
            AIService aiService,
            RequirementRepository requirementRepository,
            RequirementMappingRepository requirementMappingRepository,
            ClarificationQuestionRepository clarificationQuestionRepository,
            UnsupportedClaimRepository unsupportedClaimRepository) {
        this.assessmentService = assessmentService;
        this.summaryService = summaryService;
        this.aiService = aiService;
        this.requirementRepository = requirementRepository;
        this.requirementMappingRepository = requirementMappingRepository;
        this.clarificationQuestionRepository = clarificationQuestionRepository;
        this.unsupportedClaimRepository = unsupportedClaimRepository;
    }

    private Assessment fetchAssessment(String assessmentId) {
        Assessment a = assessmentService.getAssessment(assessmentId);
        if (a == null) {
            throw new ResourceNotFoundException("Assessment not found");
        }
        return a;
    }

    private AssessmentResponse toResponse(Assessment a) {
        boolean stale = assessmentService.isStale(a);
        return AssessmentResponse.builder()
                .id(a.getId())
                .guidelineVersionId(a.getGuidelineVersionId())
                .applicationVersionId(a.getApplicationVersionId())
                .status(a.getStatus())
                .isStale(stale)
                .analyzedAt(a.getAnalyzedAt())
                .failureReason(a.getFailureReason())
                .createdAt(a.getCreatedAt())
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentResponse createAssessment(@RequestBody CreateAssessmentRequest body) {
        Assessment a = assessmentService.createAssessment(
                body.getGuidelineVersionId(),
                body.getApplicationVersionId());
        return toResponse(a);
    }

    @GetMapping("/{assessmentId}")
    public AssessmentResponse getAssessment(@PathVariable String assessmentId) {
        Assessment a = fetchAssessment(assessmentId);
        return toResponse(a);
    }

    @PostMapping("/{assessmentId}/analyze")
    public AssessmentResponse analyzeAssessment(@PathVariable String assessmentId) {
        Assessment a = fetchAssessment(assessmentId);
        if ("analyzing".equals(a.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Assessment is already being analyzed");
        }
        try {
            assessmentService.runPipeline(a, aiService);
        } catch (Exception e) {
            // Pipeline failure is already recorded; return the assessment (failed status)
        }
        a = fetchAssessment(assessmentId);
        return toResponse(a);
    }

    @GetMapping("/{assessmentId}/summary")
    public SummaryResponse getSummary(@PathVariable String assessmentId) {
        Assessment a = fetchAssessment(assessmentId);
        if (!"ready".equals(a.getStatus()) && !"failed".equals(a.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Summary not available; assessment status is '" + a.getStatus() + "'");
        }
        return summaryService.computeSummary(a);
    }

    @GetMapping("/{assessmentId}/requirements")
    public List<RequirementResponse> getRequirements(@PathVariable String assessmentId) {
        Assessment a = fetchAssessment(assessmentId);
        List<Requirement> reqs = requirementRepository
                .findByAssessmentIdOrderByOrderIndexAsc(a.getId());
        return reqs.stream()
                .map(r -> RequirementResponse.builder()
                        .id(r.getId())
                        .assessmentId(r.getAssessmentId())
                        .text(r.getText())
                        .classification(r.getClassification())
                        .sourcePage(r.getSourcePage())
                        .sourceExcerpt(r.getSourceExcerpt())
                        .orderIndex(r.getOrderIndex())
                        .build())
                .toList();
    }

    @GetMapping("/{assessmentId}/mappings")
    public List<MappingResponse> getMappings(@PathVariable String assessmentId) {
        Assessment a = fetchAssessment(assessmentId);
        List<Requirement> reqs = requirementRepository
                .findByAssessmentIdOrderByOrderIndexAsc(a.getId());
        return reqs.stream()
                .flatMap(req -> requirementMappingRepository
                        .findByRequirementId(req.getId()).stream())
                .map(m -> MappingResponse.builder()
                        .id(m.getId())
                        .requirementId(m.getRequirementId())
                        .evidenceItemId(m.getEvidenceItemId())
                        .aiConfidence(m.getAiConfidence())
                        .aiExplanation(m.getAiExplanation())
                        .guidelineCitation(m.getGuidelineCitation())
                        .aiCitation(m.getAiCitation())
                        .reviewStatus(m.getReviewStatus())
                        .reviewerNote(m.getReviewerNote())
                        .reviewedAt(m.getReviewedAt())
                        .build())
                .toList();
    }

    @GetMapping("/{assessmentId}/questions")
    public List<QuestionResponse> getQuestions(@PathVariable String assessmentId) {
        Assessment a = fetchAssessment(assessmentId);
        return clarificationQuestionRepository.findByAssessmentId(a.getId()).stream()
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .assessmentId(q.getAssessmentId())
                        .requirementId(q.getRequirementId())
                        .questionText(q.getQuestionText())
                        .resolved(q.isResolved())
                        .build())
                .toList();
    }

    @GetMapping("/{assessmentId}/claims")
    public List<ClaimResponse> getClaims(@PathVariable String assessmentId) {
        Assessment a = fetchAssessment(assessmentId);
        return unsupportedClaimRepository.findByAssessmentId(a.getId()).stream()
                .map(c -> ClaimResponse.builder()
                        .id(c.getId())
                        .assessmentId(c.getAssessmentId())
                        .claimText(c.getClaimText())
                        .sourcePage(c.getSourcePage())
                        .dismissed(c.isDismissed())
                        .build())
                .toList();
    }
}
