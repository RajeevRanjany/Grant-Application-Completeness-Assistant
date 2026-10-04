package com.grant.assistant.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtractedEvidence {

    @JsonProperty("text")
    private String text;

    @JsonProperty("source_page")
    private int sourcePage;

    @JsonProperty("source_excerpt")
    private String sourceExcerpt;

    @JsonProperty("category")
    private String category;

    public ExtractedEvidence() {}

    public String getText() { return text; }
    public void setText(String v) { this.text = v; }
    public int getSourcePage() { return sourcePage; }
    public void setSourcePage(int v) { this.sourcePage = v; }
    public String getSourceExcerpt() { return sourceExcerpt; }
    public void setSourceExcerpt(String v) { this.sourceExcerpt = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
}
