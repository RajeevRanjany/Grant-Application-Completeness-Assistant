package com.grant.assistant.dto.request;

public class UpdateSupportingDocumentRequest {
    private String name;
    private String description;
    private Boolean received;
    private String notes;

    public UpdateSupportingDocumentRequest() {}

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public Boolean getReceived() { return received; }
    public void setReceived(Boolean v) { this.received = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}
