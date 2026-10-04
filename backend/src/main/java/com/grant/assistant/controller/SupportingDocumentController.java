package com.grant.assistant.controller;

import com.grant.assistant.dto.request.CreateSupportingDocumentRequest;
import com.grant.assistant.dto.request.UpdateSupportingDocumentRequest;
import com.grant.assistant.dto.response.SupportingDocumentResponse;
import com.grant.assistant.model.SupportingDocument;
import com.grant.assistant.service.SupportingDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SupportingDocumentController {

    private final SupportingDocumentService service;

    @Autowired
    public SupportingDocumentController(SupportingDocumentService service) {
        this.service = service;
    }

    private SupportingDocumentResponse toResponse(SupportingDocument d) {
        return SupportingDocumentResponse.builder()
                .id(d.getId())
                .assessmentId(d.getAssessmentId())
                .name(d.getName())
                .description(d.getDescription())
                .received(d.isReceived())
                .receivedAt(d.getReceivedAt())
                .notes(d.getNotes())
                .createdAt(d.getCreatedAt())
                .build();
    }

    @PostMapping("/assessments/{assessmentId}/supporting-documents")
    @ResponseStatus(HttpStatus.CREATED)
    public SupportingDocumentResponse create(
            @PathVariable String assessmentId,
            @RequestBody CreateSupportingDocumentRequest body) {
        SupportingDocument created = service.create(
                assessmentId, body.getName(), body.getDescription());
        return toResponse(created);
    }

    @GetMapping("/assessments/{assessmentId}/supporting-documents")
    public List<SupportingDocumentResponse> list(@PathVariable String assessmentId) {
        return service.listForAssessment(assessmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/supporting-documents/{docId}")
    public SupportingDocumentResponse update(
            @PathVariable String docId,
            @RequestBody UpdateSupportingDocumentRequest body) {
        SupportingDocument updated = service.update(
                docId, body.getName(), body.getDescription(), body.getReceived(), body.getNotes());
        return toResponse(updated);
    }

    @DeleteMapping("/supporting-documents/{docId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String docId) {
        service.delete(docId);
    }
}
