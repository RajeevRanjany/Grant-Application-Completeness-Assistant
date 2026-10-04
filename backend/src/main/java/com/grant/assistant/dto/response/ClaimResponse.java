package com.grant.assistant.dto.response;

public class ClaimResponse {
    private String id;
    private String assessmentId;
    private String claimText;
    private Integer sourcePage;
    private boolean dismissed;

    public ClaimResponse() {}

    private ClaimResponse(Builder b) {
        this.id = b.id;
        this.assessmentId = b.assessmentId;
        this.claimText = b.claimText;
        this.sourcePage = b.sourcePage;
        this.dismissed = b.dismissed;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public String getAssessmentId() { return assessmentId; }
    public String getClaimText() { return claimText; }
    public Integer getSourcePage() { return sourcePage; }
    public boolean isDismissed() { return dismissed; }

    public static class Builder {
        private String id;
        private String assessmentId;
        private String claimText;
        private Integer sourcePage;
        private boolean dismissed;

        public Builder id(String id) { this.id = id; return this; }
        public Builder assessmentId(String v) { this.assessmentId = v; return this; }
        public Builder claimText(String v) { this.claimText = v; return this; }
        public Builder sourcePage(Integer v) { this.sourcePage = v; return this; }
        public Builder dismissed(boolean v) { this.dismissed = v; return this; }
        public ClaimResponse build() { return new ClaimResponse(this); }
    }
}
