package com.grant.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_pages")
public class DocumentPage {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "document_version_id", length = 32, nullable = false)
    private String documentVersionId;

    @Column(name = "page_number", nullable = false)
    private int pageNumber;

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    public DocumentPage() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDocumentVersionId() { return documentVersionId; }
    public void setDocumentVersionId(String v) { this.documentVersionId = v; }
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int v) { this.pageNumber = v; }
    public String getText() { return text; }
    public void setText(String v) { this.text = v; }
}
