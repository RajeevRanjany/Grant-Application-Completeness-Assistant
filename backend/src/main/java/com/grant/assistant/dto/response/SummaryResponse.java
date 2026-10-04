package com.grant.assistant.dto.response;

import java.util.List;

public class SummaryResponse {
    private String assessmentId;
    private int totalRequirements;
    private int satisfied;
    private int weak;
    private int ambiguous;
    private int missing;
    private int completionPct;
    private List<RequirementSummaryItem> requirements;

    public SummaryResponse() {}

    private SummaryResponse(Builder b) {
        this.assessmentId = b.assessmentId;
        this.totalRequirements = b.totalRequirements;
        this.satisfied = b.satisfied;
        this.weak = b.weak;
        this.ambiguous = b.ambiguous;
        this.missing = b.missing;
        this.completionPct = b.completionPct;
        this.requirements = b.requirements;
    }

    public static Builder builder() { return new Builder(); }

    public String getAssessmentId() { return assessmentId; }
    public int getTotalRequirements() { return totalRequirements; }
    public int getSatisfied() { return satisfied; }
    public int getWeak() { return weak; }
    public int getAmbiguous() { return ambiguous; }
    public int getMissing() { return missing; }
    public int getCompletionPct() { return completionPct; }
    public List<RequirementSummaryItem> getRequirements() { return requirements; }

    public static class Builder {
        private String assessmentId;
        private int totalRequirements;
        private int satisfied;
        private int weak;
        private int ambiguous;
        private int missing;
        private int completionPct;
        private List<RequirementSummaryItem> requirements;

        public Builder assessmentId(String v) { this.assessmentId = v; return this; }
        public Builder totalRequirements(int v) { this.totalRequirements = v; return this; }
        public Builder satisfied(int v) { this.satisfied = v; return this; }
        public Builder weak(int v) { this.weak = v; return this; }
        public Builder ambiguous(int v) { this.ambiguous = v; return this; }
        public Builder missing(int v) { this.missing = v; return this; }
        public Builder completionPct(int v) { this.completionPct = v; return this; }
        public Builder requirements(List<RequirementSummaryItem> v) { this.requirements = v; return this; }
        public SummaryResponse build() { return new SummaryResponse(this); }
    }

    public static class RequirementSummaryItem {
        private String requirementId;
        private String text;
        private String classification;
        private String status;

        public RequirementSummaryItem() {}

        private RequirementSummaryItem(ItemBuilder b) {
            this.requirementId = b.requirementId;
            this.text = b.text;
            this.classification = b.classification;
            this.status = b.status;
        }

        public static ItemBuilder builder() { return new ItemBuilder(); }

        public String getRequirementId() { return requirementId; }
        public String getText() { return text; }
        public String getClassification() { return classification; }
        public String getStatus() { return status; }

        public static class ItemBuilder {
            private String requirementId;
            private String text;
            private String classification;
            private String status;

            public ItemBuilder requirementId(String v) { this.requirementId = v; return this; }
            public ItemBuilder text(String v) { this.text = v; return this; }
            public ItemBuilder classification(String v) { this.classification = v; return this; }
            public ItemBuilder status(String v) { this.status = v; return this; }
            public RequirementSummaryItem build() { return new RequirementSummaryItem(this); }
        }
    }
}
