package com.grant.assistant.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "requirement_mappings")
public class RequirementMapping {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "requirement_id", length = 32, nullable = false)
    private String requirementId;

    @Column(name = "evidence_item_id", length = 32)
    private String evidenceItemId;

    @Column(name = "ai_confidence", length = 20, nullable = false)
    private String aiConfidence = "none";

    @Column(name = "ai_explanation", columnDefinition = "TEXT")
    private String aiExplanation;

    @Column(name = "guideline_citation", columnDefinition = "TEXT")
    private String guidelineCitation;

    @Column(name = "ai_citation", columnDefinition = "TEXT")
    private String aiCitation;

    @Column(name = "review_status", length = 20, nullable = false)
    private String reviewStatus = "pending";

    @Column(name = "reviewer_note", columnDefinition = "TEXT")
    private String reviewerNote;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    public RequirementMapping() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String v) { this.requirementId = v; }
    public String getEvidenceItemId() { return evidenceItemId; }
    public void setEvidenceItemId(String v) { this.evidenceItemId = v; }
    public String getAiConfidence() { return aiConfidence; }
    public void setAiConfidence(String v) { this.aiConfidence = v; }
    public String getAiExplanation() { return aiExplanation; }
    public void setAiExplanation(String v) { this.aiExplanation = v; }
    public String getGuidelineCitation() { return guidelineCitation; }
    public void setGuidelineCitation(String v) { this.guidelineCitation = v; }
    public String getAiCitation() { return aiCitation; }
    public void setAiCitation(String v) { this.aiCitation = v; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String v) { this.reviewStatus = v; }
    public String getReviewerNote() { return reviewerNote; }
    public void setReviewerNote(String v) { this.reviewerNote = v; }
    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant v) { this.reviewedAt = v; }
}
