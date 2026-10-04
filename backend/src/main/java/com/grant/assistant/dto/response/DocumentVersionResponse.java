package com.grant.assistant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class DocumentVersionResponse {
    private String id;
    private String documentType;
    private String filename;
    private int versionNumber;
    private String documentGroupId;
    private Integer pageCount;
    @JsonProperty("is_empty")
    private Boolean isEmpty;
    private Instant uploadedAt;

    public DocumentVersionResponse() {}

    private DocumentVersionResponse(Builder b) {
        this.id = b.id;
        this.documentType = b.documentType;
        this.filename = b.filename;
        this.versionNumber = b.versionNumber;
        this.documentGroupId = b.documentGroupId;
        this.pageCount = b.pageCount;
        this.isEmpty = b.isEmpty;
        this.uploadedAt = b.uploadedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public String getDocumentType() { return documentType; }
    public String getFilename() { return filename; }
    public int getVersionNumber() { return versionNumber; }
    public String getDocumentGroupId() { return documentGroupId; }
    public Integer getPageCount() { return pageCount; }
    @JsonProperty("is_empty")
    public Boolean getIsEmpty() { return isEmpty; }
    public Instant getUploadedAt() { return uploadedAt; }

    public static class Builder {
        private String id;
        private String documentType;
        private String filename;
        private int versionNumber;
        private String documentGroupId;
        private Integer pageCount;
        private Boolean isEmpty;
        private Instant uploadedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder documentType(String v) { this.documentType = v; return this; }
        public Builder filename(String v) { this.filename = v; return this; }
        public Builder versionNumber(int v) { this.versionNumber = v; return this; }
        public Builder documentGroupId(String v) { this.documentGroupId = v; return this; }
        public Builder pageCount(Integer v) { this.pageCount = v; return this; }
        public Builder isEmpty(Boolean v) { this.isEmpty = v; return this; }
        public Builder uploadedAt(Instant v) { this.uploadedAt = v; return this; }
        public DocumentVersionResponse build() { return new DocumentVersionResponse(this); }
    }
}
