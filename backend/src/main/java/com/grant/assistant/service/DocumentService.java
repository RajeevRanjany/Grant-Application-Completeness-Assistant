package com.grant.assistant.service;

import com.grant.assistant.exception.ResourceNotFoundException;
import com.grant.assistant.model.DocumentPage;
import com.grant.assistant.model.DocumentVersion;
import com.grant.assistant.processing.PdfExtractor;
import com.grant.assistant.repository.DocumentPageRepository;
import com.grant.assistant.repository.DocumentVersionRepository;
import com.grant.assistant.service.ai.dto.PageContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentPageRepository documentPageRepository;
    private final PdfExtractor pdfExtractor;
    private final String uploadDir;

    public DocumentService(
            DocumentVersionRepository documentVersionRepository,
            DocumentPageRepository documentPageRepository,
            PdfExtractor pdfExtractor,
            @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.documentVersionRepository = documentVersionRepository;
        this.documentPageRepository = documentPageRepository;
        this.pdfExtractor = pdfExtractor;
        this.uploadDir = uploadDir;
    }

    @Transactional
    public DocumentVersion createDocumentVersion(
            byte[] content,
            String filename,
            String documentType,
            String groupId) {

        List<PageContent> pages = pdfExtractor.extractPages(content);

        String resolvedGroupId;
        int versionNumber;

        if (groupId == null) {
            resolvedGroupId = UUID.randomUUID().toString().replace("-", "");
            versionNumber = 1;
        } else {
            if (!documentVersionRepository.existsByDocumentGroupId(groupId)) {
                throw new ResourceNotFoundException("No documents found for group " + groupId);
            }
            int currentMax = documentVersionRepository
                    .findMaxVersionNumberByGroupId(groupId)
                    .orElse(0);
            versionNumber = currentMax + 1;
            resolvedGroupId = groupId;
        }

        Path dest = Paths.get(uploadDir);
        try {
            Files.createDirectories(dest);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + e.getMessage(), e);
        }
        String savedFileName = UUID.randomUUID().toString().replace("-", "") + ".pdf";
        Path filePath = dest.resolve(savedFileName);
        try {
            Files.write(filePath, content);
        } catch (IOException e) {
            throw new RuntimeException("Could not save uploaded file: " + e.getMessage(), e);
        }

        String fullText = pages.stream()
                .map(PageContent::getText)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n\n" + b);

        DocumentVersion docVersion = new DocumentVersion();
        docVersion.setId(UUID.randomUUID().toString().replace("-", ""));
        docVersion.setDocumentType(documentType);
        docVersion.setFilename(filename);
        docVersion.setFilePath(filePath.toString());
        docVersion.setVersionNumber(versionNumber);
        docVersion.setDocumentGroupId(resolvedGroupId);
        docVersion.setExtractedText(fullText);
        docVersion.setPageCount(pages.size());
        docVersion.setEmpty(false);

        documentVersionRepository.save(docVersion);

        for (PageContent page : pages) {
            DocumentPage docPage = new DocumentPage();
            docPage.setId(UUID.randomUUID().toString().replace("-", ""));
            docPage.setDocumentVersionId(docVersion.getId());
            docPage.setPageNumber(page.getPageNumber());
            docPage.setText(page.getText());
            documentPageRepository.save(docPage);
        }

        return docVersion;
    }

    @Transactional(readOnly = true)
    public DocumentVersion getDocumentVersion(String versionId) {
        return documentVersionRepository.findById(versionId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<DocumentPage> getDocumentPages(String versionId) {
        return documentPageRepository.findByDocumentVersionIdOrderByPageNumberAsc(versionId);
    }
}
