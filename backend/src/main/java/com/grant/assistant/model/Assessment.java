package com.grant.assistant.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "guideline_version_id", length = 32, nullable = false)
    private String guidelineVersionId;

    @Column(name = "application_version_id", length = 32, nullable = false)
    private String applicationVersionId;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending";

    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Assessment() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGuidelineVersionId() { return guidelineVersionId; }
    public void setGuidelineVersionId(String v) { this.guidelineVersionId = v; }
    public String getApplicationVersionId() { return applicationVersionId; }
    public void setApplicationVersionId(String v) { this.applicationVersionId = v; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(Instant v) { this.analyzedAt = v; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String v) { this.failureReason = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
}
