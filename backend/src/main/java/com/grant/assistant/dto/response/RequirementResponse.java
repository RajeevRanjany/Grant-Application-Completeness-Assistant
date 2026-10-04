package com.grant.assistant.dto.response;

public class RequirementResponse {
    private String id;
    private String assessmentId;
    private String text;
    private String classification;
    private Integer sourcePage;
    private String sourceExcerpt;
    private int orderIndex;

    public RequirementResponse() {}

    private RequirementResponse(Builder b) {
        this.id = b.id;
        this.assessmentId = b.assessmentId;
        this.text = b.text;
        this.classification = b.classification;
        this.sourcePage = b.sourcePage;
        this.sourceExcerpt = b.sourceExcerpt;
        this.orderIndex = b.orderIndex;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public String getAssessmentId() { return assessmentId; }
    public String getText() { return text; }
    public String getClassification() { return classification; }
    public Integer getSourcePage() { return sourcePage; }
    public String getSourceExcerpt() { return sourceExcerpt; }
    public int getOrderIndex() { return orderIndex; }

    public static class Builder {
        private String id;
        private String assessmentId;
        private String text;
        private String classification;
        private Integer sourcePage;
        private String sourceExcerpt;
        private int orderIndex;

        public Builder id(String id) { this.id = id; return this; }
        public Builder assessmentId(String v) { this.assessmentId = v; return this; }
        public Builder text(String v) { this.text = v; return this; }
        public Builder classification(String v) { this.classification = v; return this; }
        public Builder sourcePage(Integer v) { this.sourcePage = v; return this; }
        public Builder sourceExcerpt(String v) { this.sourceExcerpt = v; return this; }
        public Builder orderIndex(int v) { this.orderIndex = v; return this; }
        public RequirementResponse build() { return new RequirementResponse(this); }
    }
}
