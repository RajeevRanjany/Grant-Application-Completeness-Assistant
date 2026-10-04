package com.grant.assistant.dto.request;

public class CreateAssessmentRequest {
    private String guidelineVersionId;
    private String applicationVersionId;

    public CreateAssessmentRequest() {}

    public String getGuidelineVersionId() { return guidelineVersionId; }
    public void setGuidelineVersionId(String v) { this.guidelineVersionId = v; }
    public String getApplicationVersionId() { return applicationVersionId; }
    public void setApplicationVersionId(String v) { this.applicationVersionId = v; }
}
