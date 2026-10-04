package com.grant.assistant.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "supporting_documents")
public class SupportingDocument {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "assessment_id", length = 32, nullable = false)
    private String assessmentId;

    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "received", nullable = false)
    private boolean received = false;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public SupportingDocument() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String v) { this.assessmentId = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public boolean isReceived() { return received; }
    public void setReceived(boolean v) { this.received = v; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant v) { this.receivedAt = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
}
