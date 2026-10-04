package com.grant.assistant.dto.response;

public class QuestionResponse {
    private String id;
    private String assessmentId;
    private String requirementId;
    private String questionText;
    private boolean resolved;

    public QuestionResponse() {}

    private QuestionResponse(Builder b) {
        this.id = b.id;
        this.assessmentId = b.assessmentId;
        this.requirementId = b.requirementId;
        this.questionText = b.questionText;
        this.resolved = b.resolved;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public String getAssessmentId() { return assessmentId; }
    public String getRequirementId() { return requirementId; }
    public String getQuestionText() { return questionText; }
    public boolean isResolved() { return resolved; }

    public static class Builder {
        private String id;
        private String assessmentId;
        private String requirementId;
        private String questionText;
        private boolean resolved;

        public Builder id(String id) { this.id = id; return this; }
        public Builder assessmentId(String v) { this.assessmentId = v; return this; }
        public Builder requirementId(String v) { this.requirementId = v; return this; }
        public Builder questionText(String v) { this.questionText = v; return this; }
        public Builder resolved(boolean v) { this.resolved = v; return this; }
        public QuestionResponse build() { return new QuestionResponse(this); }
    }
}
