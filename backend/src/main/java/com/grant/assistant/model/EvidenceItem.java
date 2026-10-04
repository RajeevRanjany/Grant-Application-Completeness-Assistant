package com.grant.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evidence_items")
public class EvidenceItem {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "assessment_id", length = 32, nullable = false)
    private String assessmentId;

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "source_page")
    private Integer sourcePage;

    @Column(name = "source_excerpt", columnDefinition = "TEXT")
    private String sourceExcerpt;

    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    public EvidenceItem() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String v) { this.assessmentId = v; }
    public String getText() { return text; }
    public void setText(String v) { this.text = v; }
    public Integer getSourcePage() { return sourcePage; }
    public void setSourcePage(Integer v) { this.sourcePage = v; }
    public String getSourceExcerpt() { return sourceExcerpt; }
    public void setSourceExcerpt(String v) { this.sourceExcerpt = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int v) { this.orderIndex = v; }
}
