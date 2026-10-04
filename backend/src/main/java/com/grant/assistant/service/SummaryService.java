package com.grant.assistant.service;

import com.grant.assistant.dto.response.SummaryResponse;
import com.grant.assistant.model.Assessment;
import com.grant.assistant.model.Requirement;
import com.grant.assistant.model.RequirementMapping;
import com.grant.assistant.repository.RequirementMappingRepository;
import com.grant.assistant.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummaryService {

    private final RequirementRepository requirementRepository;
    private final RequirementMappingRepository requirementMappingRepository;

    @Autowired
    public SummaryService(
            RequirementRepository requirementRepository,
            RequirementMappingRepository requirementMappingRepository) {
        this.requirementRepository = requirementRepository;
        this.requirementMappingRepository = requirementMappingRepository;
    }

    public static String getRequirementStatus(List<String[]> mappingPairs) {
        if (mappingPairs == null || mappingPairs.isEmpty()) {
            return "missing";
        }

        // 1. Reviewer-confirmed/corrected takes highest priority
        for (String[] pair : mappingPairs) {
            String rs = pair[0];
            if ("confirmed".equals(rs) || "corrected".equals(rs)) {
                return "satisfied";
            }
        }

        // 2. All rejected → missing
        boolean allRejected = mappingPairs.stream()
                .allMatch(pair -> "rejected".equals(pair[0]));
        if (allRejected) {
            return "missing";
        }

        // 3. Fall through to pending mappings — pick the best available AI confidence
        List<String> pendingConfidences = new ArrayList<>();
        for (String[] pair : mappingPairs) {
            if ("pending".equals(pair[0])) {
                pendingConfidences.add(pair[1]);
            }
        }
        if (pendingConfidences.isEmpty()) {
            return "missing";
        }
        if (pendingConfidences.contains("strong")) {
            return "satisfied";
        }
        if (pendingConfidences.contains("weak")) {
            return "weak";
        }
        if (pendingConfidences.contains("ambiguous")) {
            return "ambiguous";
        }
        return "missing";
    }

    @Transactional(readOnly = true)
    public SummaryResponse computeSummary(Assessment assessment) {
        List<Requirement> requirements = requirementRepository
                .findByAssessmentIdOrderByOrderIndexAsc(assessment.getId());

        Map<String, Integer> counts = new HashMap<>();
        counts.put("satisfied", 0);
        counts.put("weak", 0);
        counts.put("ambiguous", 0);
        counts.put("missing", 0);

        List<SummaryResponse.RequirementSummaryItem> reqStatuses = new ArrayList<>();

        for (Requirement req : requirements) {
            List<RequirementMapping> mappings = requirementMappingRepository
                    .findByRequirementId(req.getId());

            List<String[]> pairs = mappings.stream()
                    .map(m -> new String[]{m.getReviewStatus(), m.getAiConfidence()})
                    .toList();

            String status = getRequirementStatus(pairs);
            counts.merge(status, 1, Integer::sum);

            reqStatuses.add(SummaryResponse.RequirementSummaryItem.builder()
                    .requirementId(req.getId())
                    .text(req.getText())
                    .classification(req.getClassification())
                    .status(status)
                    .build());
        }

        int total = requirements.size();
        int satisfied = counts.getOrDefault("satisfied", 0);
        int completionPct = total > 0 ? Math.round((float) satisfied / total * 100) : 0;

        return SummaryResponse.builder()
                .assessmentId(assessment.getId())
                .totalRequirements(total)
                .satisfied(satisfied)
                .weak(counts.getOrDefault("weak", 0))
                .ambiguous(counts.getOrDefault("ambiguous", 0))
                .missing(counts.getOrDefault("missing", 0))
                .completionPct(completionPct)
                .requirements(reqStatuses)
                .build();
    }
}
