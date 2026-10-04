package com.grant.assistant.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "document_versions")
public class DocumentVersion {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "document_type", length = 20, nullable = false)
    private String documentType;

    @Column(name = "filename", columnDefinition = "TEXT", nullable = false)
    private String filename;

    @Column(name = "file_path", columnDefinition = "TEXT", nullable = false)
    private String filePath;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "document_group_id", length = 32, nullable = false)
    private String documentGroupId;

    @Column(name = "extracted_text", columnDefinition = "TEXT")
    private String extractedText;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "is_empty", nullable = false)
    private boolean empty = false;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    public DocumentVersion() {}

    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String v) { this.documentType = v; }
    public String getFilename() { return filename; }
    public void setFilename(String v) { this.filename = v; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String v) { this.filePath = v; }
    public int getVersionNumber() { return versionNumber; }
    public void setVersionNumber(int v) { this.versionNumber = v; }
    public String getDocumentGroupId() { return documentGroupId; }
    public void setDocumentGroupId(String v) { this.documentGroupId = v; }
    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String v) { this.extractedText = v; }
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer v) { this.pageCount = v; }
    public boolean isEmpty() { return empty; }
    public void setEmpty(boolean v) { this.empty = v; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant v) { this.uploadedAt = v; }
}
