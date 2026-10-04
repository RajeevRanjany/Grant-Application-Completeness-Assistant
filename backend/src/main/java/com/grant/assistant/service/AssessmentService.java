package com.grant.assistant.service;

import com.grant.assistant.exception.ResourceNotFoundException;
import com.grant.assistant.model.*;
import com.grant.assistant.repository.*;
import com.grant.assistant.service.ai.AIService;
import com.grant.assistant.service.ai.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

    private static final Logger log = LoggerFactory.getLogger(AssessmentService.class);

    private final AssessmentRepository assessmentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentPageRepository documentPageRepository;
    private final RequirementRepository requirementRepository;
    private final EvidenceItemRepository evidenceItemRepository;
    private final RequirementMappingRepository requirementMappingRepository;
    private final ClarificationQuestionRepository clarificationQuestionRepository;
    private final UnsupportedClaimRepository unsupportedClaimRepository;

    @Autowired
    public AssessmentService(
            AssessmentRepository assessmentRepository,
            DocumentVersionRepository documentVersionRepository,
            DocumentPageRepository documentPageRepository,
            RequirementRepository requirementRepository,
            EvidenceItemRepository evidenceItemRepository,
            RequirementMappingRepository requirementMappingRepository,
            ClarificationQuestionRepository clarificationQuestionRepository,
            UnsupportedClaimRepository unsupportedClaimRepository) {
        this.assessmentRepository = assessmentRepository;
        this.documentVersionRepository = documentVersionRepository;
        this.documentPageRepository = documentPageRepository;
        this.requirementRepository = requirementRepository;
        this.evidenceItemRepository = evidenceItemRepository;
        this.requirementMappingRepository = requirementMappingRepository;
        this.clarificationQuestionRepository = clarificationQuestionRepository;
        this.unsupportedClaimRepository = unsupportedClaimRepository;
    }

    @Transactional
    public Assessment createAssessment(String guidelineVersionId, String applicationVersionId) {
        DocumentVersion guideline = documentVersionRepository.findById(guidelineVersionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Guideline version " + guidelineVersionId + " not found"));

        if (!"guideline".equals(guideline.getDocumentType())) {
            throw new IllegalArgumentException(
                    "guideline_version_id does not refer to a guideline document");
        }

        DocumentVersion application = documentVersionRepository.findById(applicationVersionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application version " + applicationVersionId + " not found"));

        if (!"application".equals(application.getDocumentType())) {
            throw new IllegalArgumentException(
                    "application_version_id does not refer to an application document");
        }

        Assessment assessment = new Assessment();
        assessment.setId(UUID.randomUUID().toString().replace("-", ""));
        assessment.setGuidelineVersionId(guidelineVersionId);
        assessment.setApplicationVersionId(applicationVersionId);
        assessment.setStatus("pending");

        return assessmentRepository.save(assessment);
    }

    @Transactional(readOnly = true)
    public Assessment getAssessment(String assessmentId) {
        return assessmentRepository.findById(assessmentId).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean isStale(Assessment assessment) {
        DocumentVersion guidelineVersion = documentVersionRepository
                .findById(assessment.getGuidelineVersionId())
                .orElse(null);
        DocumentVersion applicationVersion = documentVersionRepository
                .findById(assessment.getApplicationVersionId())
                .orElse(null);

        if (guidelineVersion == null || applicationVersion == null) {
            return false;
        }

        int maxG = documentVersionRepository
                .findMaxVersionNumberByGroupId(guidelineVersion.getDocumentGroupId())
                .orElse(0);
        int maxA = documentVersionRepository
                .findMaxVersionNumberByGroupId(applicationVersion.getDocumentGroupId())
                .orElse(0);

        return guidelineVersion.getVersionNumber() < maxG ||
               applicationVersion.getVersionNumber() < maxA;
    }

    @Transactional
    public void runPipeline(Assessment assessment, AIService aiService) {
        assessment.setStatus("analyzing");
        assessment.setFailureReason(null);
        assessmentRepository.save(assessment);

        try {
            DocumentVersion guidelineVersion = documentVersionRepository
                    .findById(assessment.getGuidelineVersionId())
                    .orElseThrow(() -> new RuntimeException("Guideline version not found"));
            DocumentVersion applicationVersion = documentVersionRepository
                    .findById(assessment.getApplicationVersionId())
                    .orElseThrow(() -> new RuntimeException("Application version not found"));

            List<DocumentPage> gPageEntities = documentPageRepository
                    .findByDocumentVersionIdOrderByPageNumberAsc(guidelineVersion.getId());
            List<DocumentPage> aPageEntities = documentPageRepository
                    .findByDocumentVersionIdOrderByPageNumberAsc(applicationVersion.getId());

            List<PageContent> gPages = gPageEntities.stream()
                    .map(p -> new PageContent(p.getPageNumber(), p.getText()))
                    .toList();
            List<PageContent> aPages = aPageEntities.stream()
                    .map(p -> new PageContent(p.getPageNumber(), p.getText()))
                    .toList();

            List<ExtractedRequirement> extractedReqs = aiService.extractRequirements(gPages);
            List<ExtractedEvidence> extractedEv = aiService.extractEvidence(aPages);

            List<Requirement> reqModels = new ArrayList<>();
            for (int i = 0; i < extractedReqs.size(); i++) {
                ExtractedRequirement r = extractedReqs.get(i);
                Requirement req = new Requirement();
                req.setId(UUID.randomUUID().toString().replace("-", ""));
                req.setAssessmentId(assessment.getId());
                req.setText(r.getText());
                req.setClassification(r.getClassification());
                req.setSourcePage(r.getSourcePage());
                req.setSourceExcerpt(r.getSourceExcerpt());
                req.setOrderIndex(i);
                requirementRepository.save(req);
                reqModels.add(req);
            }

            List<EvidenceItem> evModels = new ArrayList<>();
            for (int i = 0; i < extractedEv.size(); i++) {
                ExtractedEvidence e = extractedEv.get(i);
                EvidenceItem ev = new EvidenceItem();
                ev.setId(UUID.randomUUID().toString().replace("-", ""));
                ev.setAssessmentId(assessment.getId());
                ev.setText(e.getText());
                ev.setSourcePage(e.getSourcePage());
                ev.setSourceExcerpt(e.getSourceExcerpt());
                ev.setCategory(e.getCategory());
                ev.setOrderIndex(i);
                evidenceItemRepository.save(ev);
                evModels.add(ev);
            }

            List<RequirementMappingAI> aiMappings = aiService.mapRequirements(extractedReqs, extractedEv);

            for (RequirementMappingAI m : aiMappings) {
                Requirement reqModel = reqModels.get(m.getRequirementIndex());
                List<Integer> evidenceIndices = m.getEvidenceIndices();

                if (evidenceIndices != null && !evidenceIndices.isEmpty()) {
                    for (int evIdx : evidenceIndices) {
                        RequirementMapping mapping = new RequirementMapping();
                        mapping.setId(UUID.randomUUID().toString().replace("-", ""));
                        mapping.setRequirementId(reqModel.getId());
                        mapping.setEvidenceItemId(evModels.get(evIdx).getId());
                        mapping.setAiConfidence(m.getConfidence());
                        mapping.setAiExplanation(m.getExplanation());
                        mapping.setGuidelineCitation(m.getGuidelineCitation());
                        String appCitation = m.getApplicationCitation();
                        mapping.setAiCitation((appCitation != null && !appCitation.isBlank()) ? appCitation : null);
                        mapping.setReviewStatus("pending");
                        requirementMappingRepository.save(mapping);
                    }
                } else {
                    RequirementMapping mapping = new RequirementMapping();
                    mapping.setId(UUID.randomUUID().toString().replace("-", ""));
                    mapping.setRequirementId(reqModel.getId());
                    mapping.setEvidenceItemId(null);
                    mapping.setAiConfidence(m.getConfidence());
                    mapping.setAiExplanation(m.getExplanation());
                    mapping.setGuidelineCitation(m.getGuidelineCitation());
                    String appCitation = m.getApplicationCitation();
                    mapping.setAiCitation((appCitation != null && !appCitation.isBlank()) ? appCitation : null);
                    mapping.setReviewStatus("pending");
                    requirementMappingRepository.save(mapping);
                }
            }

            List<GapResult> gaps = aiService.detectGaps(extractedReqs, aiMappings);
            List<AIQuestion> questions = aiService.generateClarificationQuestions(extractedReqs, gaps);

            for (AIQuestion q : questions) {
                ClarificationQuestion cq = new ClarificationQuestion();
                cq.setId(UUID.randomUUID().toString().replace("-", ""));
                cq.setAssessmentId(assessment.getId());
                cq.setRequirementId(reqModels.get(q.getRequirementIndex()).getId());
                cq.setQuestionText(q.getQuestionText());
                cq.setResolved(false);
                clarificationQuestionRepository.save(cq);
            }

            List<UnsupportedClaimResult> claims = aiService.detectUnsupportedClaims(aPages, extractedEv);

            for (UnsupportedClaimResult c : claims) {
                UnsupportedClaim claim = new UnsupportedClaim();
                claim.setId(UUID.randomUUID().toString().replace("-", ""));
                claim.setAssessmentId(assessment.getId());
                claim.setClaimText(c.getClaimText());
                claim.setSourcePage(c.getSourcePage());
                claim.setDismissed(false);
                unsupportedClaimRepository.save(claim);
            }

            assessment.setStatus("ready");
            assessment.setAnalyzedAt(Instant.now());
            assessmentRepository.save(assessment);

        } catch (Exception exc) {
            log.error("Assessment pipeline failed for {}", assessment.getId(), exc);
            assessment.setStatus("failed");
            assessment.setFailureReason(exc.getMessage());
            assessmentRepository.save(assessment);
            throw exc;
        }
    }
}
