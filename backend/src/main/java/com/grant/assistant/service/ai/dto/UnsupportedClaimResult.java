package com.grant.assistant.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnsupportedClaimResult {

    @JsonProperty("claim_text")
    private String claimText;

    @JsonProperty("source_page")
    private int sourcePage;

    @JsonProperty("explanation")
    private String explanation;

    public UnsupportedClaimResult() {}

    public String getClaimText() { return claimText; }
    public void setClaimText(String v) { this.claimText = v; }
    public int getSourcePage() { return sourcePage; }
    public void setSourcePage(int v) { this.sourcePage = v; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String v) { this.explanation = v; }
}
