package com.grant.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clarification_questions")
public class ClarificationQuestion {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "assessment_id", length = 32, nullable = false)
    private String assessmentId;

    @Column(name = "requirement_id", length = 32, nullable = false)
    private String requirementId;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "resolved", nullable = false)
    private boolean resolved = false;

    public ClarificationQuestion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String v) { this.assessmentId = v; }
    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String v) { this.requirementId = v; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String v) { this.questionText = v; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean v) { this.resolved = v; }
}
