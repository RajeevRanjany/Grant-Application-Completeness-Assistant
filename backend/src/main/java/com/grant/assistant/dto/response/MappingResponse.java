package com.grant.assistant.dto.response;

import java.time.Instant;

public class MappingResponse {
    private String id;
    private String requirementId;
    private String evidenceItemId;
    private String aiConfidence;
    private String aiExplanation;
    private String guidelineCitation;
    private String aiCitation;
    private String reviewStatus;
    private String reviewerNote;
    private Instant reviewedAt;

    public MappingResponse() {}

    private MappingResponse(Builder b) {
        this.id = b.id;
        this.requirementId = b.requirementId;
        this.evidenceItemId = b.evidenceItemId;
        this.aiConfidence = b.aiConfidence;
        this.aiExplanation = b.aiExplanation;
        this.guidelineCitation = b.guidelineCitation;
        this.aiCitation = b.aiCitation;
        this.reviewStatus = b.reviewStatus;
        this.reviewerNote = b.reviewerNote;
        this.reviewedAt = b.reviewedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public String getRequirementId() { return requirementId; }
    public String getEvidenceItemId() { return evidenceItemId; }
    public String getAiConfidence() { return aiConfidence; }
    public String getAiExplanation() { return aiExplanation; }
    public String getGuidelineCitation() { return guidelineCitation; }
    public String getAiCitation() { return aiCitation; }
    public String getReviewStatus() { return reviewStatus; }
    public String getReviewerNote() { return reviewerNote; }
    public Instant getReviewedAt() { return reviewedAt; }

    public static class Builder {
        private String id;
        private String requirementId;
        private String evidenceItemId;
        private String aiConfidence;
        private String aiExplanation;
        private String guidelineCitation;
        private String aiCitation;
        private String reviewStatus;
        private String reviewerNote;
        private Instant reviewedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder requirementId(String v) { this.requirementId = v; return this; }
        public Builder evidenceItemId(String v) { this.evidenceItemId = v; return this; }
        public Builder aiConfidence(String v) { this.aiConfidence = v; return this; }
        public Builder aiExplanation(String v) { this.aiExplanation = v; return this; }
        public Builder guidelineCitation(String v) { this.guidelineCitation = v; return this; }
        public Builder aiCitation(String v) { this.aiCitation = v; return this; }
        public Builder reviewStatus(String v) { this.reviewStatus = v; return this; }
        public Builder reviewerNote(String v) { this.reviewerNote = v; return this; }
        public Builder reviewedAt(Instant v) { this.reviewedAt = v; return this; }
        public MappingResponse build() { return new MappingResponse(this); }
    }
}
