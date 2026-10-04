package com.grant.assistant.service.ai.dto;

public class PageContent {
    private int pageNumber;
    private String text;

    public PageContent() {}

    public PageContent(int pageNumber, String text) {
        this.pageNumber = pageNumber;
        this.text = text;
    }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int v) { this.pageNumber = v; }
    public String getText() { return text; }
    public void setText(String v) { this.text = v; }
}
