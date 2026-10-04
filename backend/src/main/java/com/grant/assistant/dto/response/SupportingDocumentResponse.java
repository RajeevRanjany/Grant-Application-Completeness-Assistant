package com.grant.assistant.dto.response;

import java.time.Instant;

public class SupportingDocumentResponse {
    private String id;
    private String assessmentId;
    private String name;
    private String description;
    private boolean received;
    private Instant receivedAt;
    private String notes;
    private Instant createdAt;

    public SupportingDocumentResponse() {}

    private SupportingDocumentResponse(Builder b) {
        this.id = b.id;
        this.assessmentId = b.assessmentId;
        this.name = b.name;
        this.description = b.description;
        this.received = b.received;
        this.receivedAt = b.receivedAt;
        this.notes = b.notes;
        this.createdAt = b.createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public String getAssessmentId() { return assessmentId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isReceived() { return received; }
    public Instant getReceivedAt() { return receivedAt; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }

    public static class Builder {
        private String id;
        private String assessmentId;
        private String name;
        private String description;
        private boolean received;
        private Instant receivedAt;
        private String notes;
        private Instant createdAt;

        public Builder id(String v) { this.id = v; return this; }
        public Builder assessmentId(String v) { this.assessmentId = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder received(boolean v) { this.received = v; return this; }
        public Builder receivedAt(Instant v) { this.receivedAt = v; return this; }
        public Builder notes(String v) { this.notes = v; return this; }
        public Builder createdAt(Instant v) { this.createdAt = v; return this; }
        public SupportingDocumentResponse build() { return new SupportingDocumentResponse(this); }
    }
}
