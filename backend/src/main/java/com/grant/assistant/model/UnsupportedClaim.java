package com.grant.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unsupported_claims")
public class UnsupportedClaim {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "assessment_id", length = 32, nullable = false)
    private String assessmentId;

    @Column(name = "claim_text", columnDefinition = "TEXT", nullable = false)
    private String claimText;

    @Column(name = "source_page")
    private Integer sourcePage;

    @Column(name = "dismissed", nullable = false)
    private boolean dismissed = false;

    public UnsupportedClaim() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String v) { this.assessmentId = v; }
    public String getClaimText() { return claimText; }
    public void setClaimText(String v) { this.claimText = v; }
    public Integer getSourcePage() { return sourcePage; }
    public void setSourcePage(Integer v) { this.sourcePage = v; }
    public boolean isDismissed() { return dismissed; }
    public void setDismissed(boolean v) { this.dismissed = v; }
}
