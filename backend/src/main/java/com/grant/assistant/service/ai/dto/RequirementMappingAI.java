package com.grant.assistant.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RequirementMappingAI {

    @JsonProperty("requirement_index")
    private int requirementIndex;

    @JsonProperty("evidence_indices")
    private List<Integer> evidenceIndices;

    @JsonProperty("confidence")
    private String confidence;

    @JsonProperty("explanation")
    private String explanation;

    @JsonProperty("guideline_citation")
    private String guidelineCitation;

    @JsonProperty("application_citation")
    private String applicationCitation;

    public RequirementMappingAI() {}

    public int getRequirementIndex() { return requirementIndex; }
    public void setRequirementIndex(int v) { this.requirementIndex = v; }
    public List<Integer> getEvidenceIndices() { return evidenceIndices; }
    public void setEvidenceIndices(List<Integer> v) { this.evidenceIndices = v; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String v) { this.confidence = v; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String v) { this.explanation = v; }
    public String getGuidelineCitation() { return guidelineCitation; }
    public void setGuidelineCitation(String v) { this.guidelineCitation = v; }
    public String getApplicationCitation() { return applicationCitation; }
    public void setApplicationCitation(String v) { this.applicationCitation = v; }
}
