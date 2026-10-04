package com.grant.assistant.dto.request;

public class ReviewMappingRequest {
    private String action;
    private String reviewerNote;

    public ReviewMappingRequest() {}

    public String getAction() { return action; }
    public void setAction(String v) { this.action = v; }
    public String getReviewerNote() { return reviewerNote; }
    public void setReviewerNote(String v) { this.reviewerNote = v; }
}
