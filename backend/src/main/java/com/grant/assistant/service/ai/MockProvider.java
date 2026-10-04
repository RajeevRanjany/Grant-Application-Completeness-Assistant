package com.grant.assistant.service.ai;

import java.util.List;
import java.util.Map;

public class MockProvider implements LLMProvider {

    private static final String REQUIREMENTS_JSON = """
            [
              {
                "text": "Applicant must be a registered non-profit organization",
                "classification": "mandatory",
                "source_page": 1,
                "source_excerpt": "Applicant must be a registered non-profit organization"
              },
              {
                "text": "Project must demonstrate measurable community impact",
                "classification": "mandatory",
                "source_page": 2,
                "source_excerpt": "Project must demonstrate measurable community impact"
              },
              {
                "text": "Applicant should have operated for at least two years",
                "classification": "recommendation",
                "source_page": 1,
                "source_excerpt": "Applicant should have operated for at least two years"
              }
            ]
            """;

    private static final String EVIDENCE_JSON = """
            [
              {
                "text": "Our organization has been a registered 501(c)(3) since 2015",
                "source_page": 1,
                "source_excerpt": "Our organization has been a registered 501(c)(3) since 2015",
                "category": "organizational"
              },
              {
                "text": "We have served over 500 community members through our programs",
                "source_page": 2,
                "source_excerpt": "We have served over 500 community members through our programs",
                "category": "impact"
              }
            ]
            """;

    private static final String MAPPINGS_JSON = """
            [
              {
                "requirement_index": 0,
                "evidence_indices": [0],
                "confidence": "strong",
                "explanation": "Application directly states 501(c)(3) registration",
                "guideline_citation": "Applicant must be a registered non-profit organization",
                "application_citation": "Our organization has been a registered 501(c)(3) since 2015"
              },
              {
                "requirement_index": 1,
                "evidence_indices": [1],
                "confidence": "strong",
                "explanation": "Application provides community impact statistics",
                "guideline_citation": "Project must demonstrate measurable community impact",
                "application_citation": "We have served over 500 community members through our programs"
              },
              {
                "requirement_index": 2,
                "evidence_indices": [],
                "confidence": "weak",
                "explanation": "Application does not explicitly state years of operation",
                "guideline_citation": "Applicant should have operated for at least two years",
                "application_citation": ""
              }
            ]
            """;

    private static final String GAPS_JSON = """
            [
              {
                "requirement_index": 2,
                "confirmed_confidence": "weak",
                "explanation": "No explicit statement of operating duration found in the application"
              }
            ]
            """;

    private static final String QUESTIONS_JSON = """
            [
              {
                "requirement_index": 2,
                "question_text": "Can you confirm your organization has been operating for at least two years and provide supporting documentation such as incorporation records or annual reports?"
              }
            ]
            """;

    private static final String CLAIMS_JSON = """
            [
              {
                "claim_text": "We have the most experienced team in the region",
                "source_page": 3,
                "explanation": "No comparative evidence is provided to support this claim"
              }
            ]
            """;

    @Override
    public String complete(List<Map<String, String>> messages, String model, double temperature) {
        String system = messages.stream()
                .filter(m -> "system".equals(m.get("role")))
                .map(m -> m.get("content"))
                .findFirst()
                .orElse("");

        if (system.contains("OPERATION: requirement_extraction")) {
            return REQUIREMENTS_JSON;
        } else if (system.contains("OPERATION: evidence_extraction")) {
            return EVIDENCE_JSON;
        } else if (system.contains("OPERATION: requirement_mapping")) {
            return MAPPINGS_JSON;
        } else if (system.contains("OPERATION: gap_detection")) {
            return GAPS_JSON;
        } else if (system.contains("OPERATION: clarification_questions")) {
            return QUESTIONS_JSON;
        } else if (system.contains("OPERATION: unsupported_claim_detection")) {
            return CLAIMS_JSON;
        }

        return "[]";
    }
}
