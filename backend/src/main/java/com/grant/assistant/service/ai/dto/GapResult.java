package com.grant.assistant.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GapResult {

    @JsonProperty("requirement_index")
    private int requirementIndex;

    @JsonProperty("confirmed_confidence")
    private String confirmedConfidence;

    @JsonProperty("explanation")
    private String explanation;

    public GapResult() {}

    public int getRequirementIndex() { return requirementIndex; }
    public void setRequirementIndex(int v) { this.requirementIndex = v; }
    public String getConfirmedConfidence() { return confirmedConfidence; }
    public void setConfirmedConfidence(String v) { this.confirmedConfidence = v; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String v) { this.explanation = v; }
}
