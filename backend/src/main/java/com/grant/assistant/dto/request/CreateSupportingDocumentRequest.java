package com.grant.assistant.dto.request;

public class CreateSupportingDocumentRequest {
    private String name;
    private String description;

    public CreateSupportingDocumentRequest() {}

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
}
