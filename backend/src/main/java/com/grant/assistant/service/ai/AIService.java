package com.grant.assistant.service.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grant.assistant.service.ai.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AIService {

    private final LLMProvider provider;
    private final String model;
    private final double temperature;
    private final ObjectMapper objectMapper;

    public AIService(LLMProvider provider, String model, double temperature) {
        this.provider = provider;
        this.model = model != null ? model : "";
        this.temperature = temperature;
        this.objectMapper = new ObjectMapper();
    }

    private String call(List<Map<String, String>> messages) {
        return provider.complete(messages, model, temperature);
    }

    public static String stripMarkdownFence(String text) {
        if (text == null) return "";
        text = text.strip();
        if (text.startsWith("```")) {
            int newlineIdx = text.indexOf('\n');
            if (newlineIdx >= 0) {
                text = text.substring(newlineIdx + 1);
            } else {
                text = text.substring(3);
            }
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.lastIndexOf("```"));
        }
        return text.strip();
    }

    private <T> List<T> parseList(String response, Class<T> clazz, String operation) {
        if (response == null || response.isBlank()) {
            throw new RuntimeException("Empty response received for " + operation);
        }
        String cleaned = stripMarkdownFence(response);
        try {
            return objectMapper.readValue(cleaned,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response for " + operation + ": " + e.getMessage(), e);
        }
    }

    public List<ExtractedRequirement> extractRequirements(List<PageContent> pages) {
        List<Map<String, String>> messages = buildRequirementExtractionMessages(pages);
        String response = call(messages);
        return parseList(response, ExtractedRequirement.class, "requirement_extraction");
    }

    public List<ExtractedEvidence> extractEvidence(List<PageContent> pages) {
        List<Map<String, String>> messages = buildEvidenceExtractionMessages(pages);
        String response = call(messages);
        return parseList(response, ExtractedEvidence.class, "evidence_extraction");
    }

    public List<RequirementMappingAI> mapRequirements(
            List<ExtractedRequirement> requirements,
            List<ExtractedEvidence> evidence) {
        List<Map<String, String>> messages = buildMappingMessages(requirements, evidence);
        String response = call(messages);
        List<RequirementMappingAI> result = parseList(response, RequirementMappingAI.class, "requirement_mapping");
        validateMappingIndices(result, requirements, evidence);
        return result;
    }

    public List<GapResult> detectGaps(
            List<ExtractedRequirement> requirements,
            List<RequirementMappingAI> mappings) {
        List<RequirementMappingAI> gapMappings = mappings.stream()
                .filter(m -> {
                    String c = m.getConfidence();
                    return "weak".equals(c) || "ambiguous".equals(c) || "none".equals(c);
                })
                .toList();
        if (gapMappings.isEmpty()) {
            return List.of();
        }
        List<Map<String, String>> messages = buildGapDetectionMessages(requirements, gapMappings);
        String response = call(messages);
        return parseList(response, GapResult.class, "gap_detection");
    }

    public List<AIQuestion> generateClarificationQuestions(
            List<ExtractedRequirement> requirements,
            List<GapResult> gaps) {
        if (gaps.isEmpty()) {
            return List.of();
        }
        List<Map<String, String>> messages = buildClarificationMessages(requirements, gaps);
        String response = call(messages);
        return parseList(response, AIQuestion.class, "clarification_questions");
    }

    public List<UnsupportedClaimResult> detectUnsupportedClaims(
            List<PageContent> applicationPages,
            List<ExtractedEvidence> evidence) {
        List<Map<String, String>> messages = buildClaimDetectionMessages(applicationPages, evidence);
        String response = call(messages);
        return parseList(response, UnsupportedClaimResult.class, "unsupported_claim_detection");
    }

    private void validateMappingIndices(
            List<RequirementMappingAI> mappings,
            List<ExtractedRequirement> requirements,
            List<ExtractedEvidence> evidence) {
        int reqCount = requirements.size();
        int evCount = evidence.size();
        for (RequirementMappingAI m : mappings) {
            if (m.getRequirementIndex() < 0 || m.getRequirementIndex() >= reqCount) {
                throw new RuntimeException(
                        "Mapping references out-of-range requirement_index " + m.getRequirementIndex());
            }
            if (m.getEvidenceIndices() != null) {
                for (int ei : m.getEvidenceIndices()) {
                    if (ei < 0 || ei >= evCount) {
                        throw new RuntimeException(
                                "Mapping references out-of-range evidence_index " + ei);
                    }
                }
            }
        }
    }

    private static final String REQUIREMENT_EXTRACTION_SYSTEM = """
            OPERATION: requirement_extraction

            You are a grant compliance analyst. Extract EVERY requirement that is explicitly stated in the document text provided.

            STRICT RULES:
            - Do NOT invent, infer, or paraphrase requirements that are not present verbatim in the document.
            - "text" must be a faithful restatement of the requirement as written in the document.
            - "source_excerpt" must be a verbatim quote copied directly from the document text.
            - "source_page" must match the [Page N] label where the excerpt appears.
            - If a statement does not appear in the document, do not include it.

            COVERAGE RULES — you MUST include requirements from ALL of these sources:
            - Every numbered or bulleted requirement item (e.g., "1.", "2.", "a)", "-")
            - Items under headings such as "Eligibility", "Mandatory requirements", "Recommendations",
              "Submission requirements", "Submission", "Supporting documents", or similar.
            - Every sentence in a "Submission requirements" (or equivalent) section — even if written as prose —
              that imposes an expectation on the applicant (e.g., "Applications should be submitted as a PDF").
            - Do not merge multiple distinct requirements into one; each distinct expectation is its own object.
            - Do not drop requirements just because they are short or appear in a prose paragraph.

            CLASSIFICATION RULES:
            - "mandatory": uses must / shall / required / is required / will be required / mandatory.
            - "recommendation": uses should / encouraged / preferred / recommended.
            - A submission statement using "should" is a "recommendation".
            - A submission statement using "must" is "mandatory".

            For each requirement found, return a JSON object with:
            - "text": the requirement statement as written
            - "classification": "mandatory" or "recommendation"
            - "source_page": the page number where it appears
            - "source_excerpt": the exact verbatim sentence from the document

            Return only a JSON array of requirement objects. No prose, no markdown fences.""";

    private List<Map<String, String>> buildRequirementExtractionMessages(List<PageContent> pages) {
        StringBuilder sb = new StringBuilder();
        for (PageContent p : pages) {
            if (p.getText() != null && !p.getText().isBlank()) {
                sb.append("[Page ").append(p.getPageNumber()).append("]\n")
                  .append(p.getText().strip()).append("\n\n");
            }
        }
        String formatted = sb.toString().strip();
        return List.of(
                Map.of("role", "system", "content", REQUIREMENT_EXTRACTION_SYSTEM),
                Map.of("role", "user", "content",
                        "Extract all requirements from the following grant guideline:\n\n" + formatted)
        );
    }

    private static final String EVIDENCE_EXTRACTION_SYSTEM = """
            OPERATION: evidence_extraction

            You are a grant compliance analyst. Extract ONLY evidence that is explicitly present in the application text provided.

            STRICT RULES:
            - Do NOT invent, fabricate, or paraphrase passages that are not in the document.
            - "text" must reflect what is actually written in the application.
            - "source_excerpt" must be a verbatim quote copied directly from the application text.
            - "source_page" must match the [Page N] label where the excerpt appears.
            - If a passage does not appear in the document, do not include it.

            For each piece of evidence found, return:
            - "text": the evidence passage as written
            - "source_page": page number where it appears
            - "source_excerpt": the exact verbatim excerpt from the application
            - "category": a short label such as "organizational", "financial", "impact", "technical", or null

            Return only a JSON array of evidence objects. No prose, no markdown fences.""";

    private List<Map<String, String>> buildEvidenceExtractionMessages(List<PageContent> pages) {
        StringBuilder sb = new StringBuilder();
        for (PageContent p : pages) {
            if (p.getText() != null && !p.getText().isBlank()) {
                sb.append("[Page ").append(p.getPageNumber()).append("]\n")
                  .append(p.getText().strip()).append("\n\n");
            }
        }
        String formatted = sb.toString().strip();
        return List.of(
                Map.of("role", "system", "content", EVIDENCE_EXTRACTION_SYSTEM),
                Map.of("role", "user", "content",
                        "Extract all evidence from the following grant application:\n\n" + formatted)
        );
    }

    private static final String MAPPING_SYSTEM = """
            OPERATION: requirement_mapping

            You are a grant compliance analyst. Map each grant requirement to the most relevant evidence in the application.

            For each requirement, return a JSON object with:
            - "requirement_index": 0-based index of the requirement
            - "evidence_indices": list of 0-based indices of matching evidence items (empty list if none)
            - "confidence": "strong", "weak", "ambiguous", or "none"
            - "explanation": one sentence explaining the confidence rating
            - "guideline_citation": verbatim text from the requirement
            - "application_citation": verbatim text from the best matching evidence (empty string if none)

            Confidence guide — be decisive. Do not default to "ambiguous" when evidence is explicit:
            - "strong": the application contains concrete, directly-matching evidence for the requirement.
              Examples of strong matches:
                • Requirement asks for a budget → application states a total amount AND lists cost categories.
                • Requirement asks for project duration within N months → application states a duration ≤ N months.
                • Requirement asks for an implementation plan with activities/timeline/responsible parties →
                  application contains those elements, even if phrased differently.
                • Requirement asks for target community + measurable outcomes → application describes the
                  community and gives numeric outcome targets.
                • Requirement asks for registration / authorized representative details → application names them.
                • Requirement asks for data-protection description → application describes handling and retention.
                • Requirement asks to submit as PDF / name supporting docs / budget-amount consistency →
                  mark "strong" if the application text satisfies the condition on its face.
              A match is still "strong" even if phrasing differs — look at substance, not keywords.
            - "weak": evidence is only partial or indirect — a related statement is present but a required
              element is missing (e.g., budget total is given but no cost categories).
            - "ambiguous": only use this when the application is genuinely unclear about whether it addresses
              the requirement. Do NOT use "ambiguous" as a default; prefer "strong" or "weak" when evidence exists.
            - "none": no relevant text in the application.

            Return only a JSON array of mapping objects. No prose, no markdown fences.""";

    private List<Map<String, String>> buildMappingMessages(
            List<ExtractedRequirement> requirements,
            List<ExtractedEvidence> evidence) {
        StringBuilder reqLines = new StringBuilder();
        for (int i = 0; i < requirements.size(); i++) {
            ExtractedRequirement r = requirements.get(i);
            reqLines.append("[").append(i).append("] (").append(r.getClassification()).append(") ")
                    .append(r.getText()).append("\n");
        }
        StringBuilder evLines = new StringBuilder();
        for (int i = 0; i < evidence.size(); i++) {
            ExtractedEvidence e = evidence.get(i);
            evLines.append("[").append(i).append("] (page ").append(e.getSourcePage()).append(") ")
                   .append(e.getText()).append("\n");
        }
        String userContent = "Requirements:\n" + reqLines.toString().strip() +
                "\n\nEvidence:\n" + evLines.toString().strip() +
                "\n\nMap each requirement to the evidence above.";
        return List.of(
                Map.of("role", "system", "content", MAPPING_SYSTEM),
                Map.of("role", "user", "content", userContent)
        );
    }

    private static final String GAP_DETECTION_SYSTEM = """
            OPERATION: gap_detection

            You are a grant compliance analyst reviewing whether mapped evidence genuinely satisfies each requirement.

            For each requirement listed below that has weak, ambiguous, or no evidence, confirm or adjust the confidence level.

            For each, return a JSON object with:
            - "requirement_index": 0-based index of the requirement
            - "confirmed_confidence": "weak", "ambiguous", or "none"
            - "explanation": one sentence explaining why the gap remains

            Return only a JSON array of gap objects. No prose, no markdown fences.""";

    private List<Map<String, String>> buildGapDetectionMessages(
            List<ExtractedRequirement> requirements,
            List<RequirementMappingAI> gapMappings) {
        StringBuilder lines = new StringBuilder();
        for (RequirementMappingAI m : gapMappings) {
            ExtractedRequirement req = requirements.get(m.getRequirementIndex());
            lines.append("[").append(m.getRequirementIndex()).append("] (")
                 .append(req.getClassification()).append(") ").append(req.getText()).append("\n")
                 .append("  current confidence: ").append(m.getConfidence()).append("\n")
                 .append("  mapping note: ").append(m.getExplanation()).append("\n\n");
        }
        String userContent = "Review the following under-evidenced requirements and confirm whether the gap is genuine:\n\n"
                + lines.toString().strip();
        return List.of(
                Map.of("role", "system", "content", GAP_DETECTION_SYSTEM),
                Map.of("role", "user", "content", userContent)
        );
    }

    private static final String CLARIFICATION_SYSTEM = """
            OPERATION: clarification_questions

            You are a grant compliance analyst helping an applicant strengthen their submission.

            For each identified evidence weakness, generate one specific, actionable clarification question the applicant should answer.

            Return a JSON array where each object has:
            - "requirement_index": 0-based index of the requirement
            - "question_text": the clarification question, phrased directly to the applicant

            Return only a JSON array. No prose, no markdown fences.""";

    private List<Map<String, String>> buildClarificationMessages(
            List<ExtractedRequirement> requirements,
            List<GapResult> gaps) {
        StringBuilder lines = new StringBuilder();
        for (GapResult gap : gaps) {
            ExtractedRequirement req = requirements.get(gap.getRequirementIndex());
            lines.append("[").append(gap.getRequirementIndex()).append("] ").append(req.getText()).append("\n")
                 .append("  weakness: ").append(gap.getExplanation()).append("\n\n");
        }
        String userContent = "Generate one clarification question for each weakness below:\n\n"
                + lines.toString().strip();
        return List.of(
                Map.of("role", "system", "content", CLARIFICATION_SYSTEM),
                Map.of("role", "user", "content", userContent)
        );
    }

    private static final String CLAIM_DETECTION_SYSTEM = """
            OPERATION: unsupported_claim_detection

            You are a grant compliance analyst identifying unsupported claims in a grant application.

            An unsupported claim is an assertion in the application text for which no supporting evidence has been supplied.

            STRICT RULES:
            - "claim_text" must be a verbatim quote copied directly from the application text provided below.
            - Do NOT invent or paraphrase claims that are not present in the provided application text.
            - "source_page" must match the [Page N] label where the claim appears.
            - Only flag claims that are genuinely unsubstantiated — do not flag factual statements that reference verifiable data already in the evidence list.

            For each unsupported claim, return a JSON object with:
            - "claim_text": the exact verbatim claim text from the application
            - "source_page": the page number where the claim appears
            - "explanation": one sentence explaining why the claim is unsupported

            Return only a JSON array of unsupported claim objects. No prose, no markdown fences.""";

    private List<Map<String, String>> buildClaimDetectionMessages(
            List<PageContent> applicationPages,
            List<ExtractedEvidence> evidence) {
        StringBuilder appText = new StringBuilder();
        for (PageContent p : applicationPages) {
            if (p.getText() != null && !p.getText().isBlank()) {
                appText.append("[Page ").append(p.getPageNumber()).append("]\n")
                       .append(p.getText().strip()).append("\n\n");
            }
        }
        StringBuilder evSummary = new StringBuilder();
        for (ExtractedEvidence e : evidence) {
            evSummary.append("- (page ").append(e.getSourcePage()).append(") ").append(e.getText()).append("\n");
        }
        String userContent = "Application text:\n" + appText.toString().strip() +
                "\n\nSupported evidence already identified:\n" + evSummary.toString().strip() +
                "\n\nIdentify any claims in the application that are not supported by the evidence above.";
        return List.of(
                Map.of("role", "system", "content", CLAIM_DETECTION_SYSTEM),
                Map.of("role", "user", "content", userContent)
        );
    }
}
