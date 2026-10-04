package com.grant.assistant.controller;

import com.grant.assistant.dto.response.DocumentVersionResponse;
import com.grant.assistant.exception.BadRequestException;
import com.grant.assistant.exception.ResourceNotFoundException;
import com.grant.assistant.model.DocumentPage;
import com.grant.assistant.model.DocumentVersion;
import com.grant.assistant.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    private byte[] readAndValidate(MultipartFile file, int maxMb) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only PDF files are supported");
        }
        byte[] content = file.getBytes();
        if (content.length == 0) {
            throw new BadRequestException("Uploaded file is empty");
        }
        if (content.length > (long) maxMb * 1024 * 1024) {
            throw new BadRequestException("File exceeds " + maxMb + " MB limit");
        }
        return content;
    }

    private DocumentVersionResponse toResponse(DocumentVersion doc) {
        return DocumentVersionResponse.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .filename(doc.getFilename())
                .versionNumber(doc.getVersionNumber())
                .documentGroupId(doc.getDocumentGroupId())
                .pageCount(doc.getPageCount())
                .isEmpty(doc.isEmpty())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }

    @PostMapping("/guideline")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentVersionResponse uploadGuideline(
            @RequestParam("file") MultipartFile file) throws IOException {
        byte[] content = readAndValidate(file, 20);
        DocumentVersion doc = documentService.createDocumentVersion(
                content, file.getOriginalFilename(), "guideline", null);
        return toResponse(doc);
    }

    @PostMapping("/application")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentVersionResponse uploadApplication(
            @RequestParam("file") MultipartFile file) throws IOException {
        byte[] content = readAndValidate(file, 20);
        DocumentVersion doc = documentService.createDocumentVersion(
                content, file.getOriginalFilename(), "application", null);
        return toResponse(doc);
    }

    @PostMapping("/guideline/{groupId}/version")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentVersionResponse uploadGuidelineVersion(
            @PathVariable String groupId,
            @RequestParam("file") MultipartFile file) throws IOException {
        byte[] content = readAndValidate(file, 20);
        DocumentVersion doc = documentService.createDocumentVersion(
                content, file.getOriginalFilename(), "guideline", groupId);
        return toResponse(doc);
    }

    @PostMapping("/application/{groupId}/version")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentVersionResponse uploadApplicationVersion(
            @PathVariable String groupId,
            @RequestParam("file") MultipartFile file) throws IOException {
        byte[] content = readAndValidate(file, 20);
        DocumentVersion doc = documentService.createDocumentVersion(
                content, file.getOriginalFilename(), "application", groupId);
        return toResponse(doc);
    }

    @GetMapping("/{versionId}")
    public DocumentVersionResponse getDocument(@PathVariable String versionId) {
        DocumentVersion doc = documentService.getDocumentVersion(versionId);
        if (doc == null) {
            throw new ResourceNotFoundException("Document not found");
        }
        return toResponse(doc);
    }

    @GetMapping("/{versionId}/pages")
    public Map<String, Object> getDocumentPages(@PathVariable String versionId) {
        DocumentVersion doc = documentService.getDocumentVersion(versionId);
        if (doc == null) {
            throw new ResourceNotFoundException("Document not found");
        }
        List<DocumentPage> pages = documentService.getDocumentPages(versionId);
        List<Map<String, Object>> pageList = pages.stream()
                .map(p -> Map.<String, Object>of(
                        "page_number", p.getPageNumber(),
                        "text", p.getText()))
                .toList();
        return Map.of(
                "version_id", doc.getId(),
                "filename", doc.getFilename(),
                "page_count", doc.getPageCount() != null ? doc.getPageCount() : 0,
                "pages", pageList
        );
    }
}
