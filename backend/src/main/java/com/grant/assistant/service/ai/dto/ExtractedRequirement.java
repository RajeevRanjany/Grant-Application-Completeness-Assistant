package com.grant.assistant.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtractedRequirement {

    @JsonProperty("text")
    private String text;

    @JsonProperty("classification")
    private String classification;

    @JsonProperty("source_page")
    private int sourcePage;

    @JsonProperty("source_excerpt")
    private String sourceExcerpt;

    public ExtractedRequirement() {}

    public String getText() { return text; }
    public void setText(String v) { this.text = v; }
    public String getClassification() { return classification; }
    public void setClassification(String v) { this.classification = v; }
    public int getSourcePage() { return sourcePage; }
    public void setSourcePage(int v) { this.sourcePage = v; }
    public String getSourceExcerpt() { return sourceExcerpt; }
    public void setSourceExcerpt(String v) { this.sourceExcerpt = v; }
}
