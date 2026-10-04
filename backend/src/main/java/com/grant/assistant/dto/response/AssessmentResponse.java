package com.grant.assistant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AssessmentResponse {
    private String id;
    private String guidelineVersionId;
    private String applicationVersionId;
    private String status;
    @JsonProperty("is_stale")
    private Boolean isStale;
    private Instant analyzedAt;
    private String failureReason;
    private Instant createdAt;

    public AssessmentResponse() {}

    private AssessmentResponse(Builder b) {
        this.id = b.id;
        this.guidelineVersionId = b.guidelineVersionId;
        this.applicationVersionId = b.applicationVersionId;
        this.status = b.status;
        this.isStale = b.isStale;
        this.analyzedAt = b.analyzedAt;
        this.failureReason = b.failureReason;
        this.createdAt = b.createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public String getGuidelineVersionId() { return guidelineVersionId; }
    public String getApplicationVersionId() { return applicationVersionId; }
    public String getStatus() { return status; }
    @JsonProperty("is_stale")
    public Boolean getIsStale() { return isStale; }
    public Instant getAnalyzedAt() { return analyzedAt; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }

    public static class Builder {
        private String id;
        private String guidelineVersionId;
        private String applicationVersionId;
        private String status;
        private Boolean isStale;
        private Instant analyzedAt;
        private String failureReason;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder guidelineVersionId(String v) { this.guidelineVersionId = v; return this; }
        public Builder applicationVersionId(String v) { this.applicationVersionId = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder isStale(Boolean v) { this.isStale = v; return this; }
        public Builder analyzedAt(Instant v) { this.analyzedAt = v; return this; }
        public Builder failureReason(String v) { this.failureReason = v; return this; }
        public Builder createdAt(Instant v) { this.createdAt = v; return this; }
        public AssessmentResponse build() { return new AssessmentResponse(this); }
    }
}
