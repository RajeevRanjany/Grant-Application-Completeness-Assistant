package com.grant.assistant.processing;

import com.grant.assistant.service.ai.dto.PageContent;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfExtractor {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractor.class);

    public List<PageContent> extractPages(byte[] content) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("File is empty");
        }

        PDDocument document;
        try {
            document = Loader.loadPDF(content);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not open file as PDF: " + e.getMessage(), e);
        }

        try {
            int pageCount = document.getNumberOfPages();
            if (pageCount == 0) {
                throw new IllegalArgumentException("PDF has no pages");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            List<PageContent> pages = new ArrayList<>();

            for (int i = 1; i <= pageCount; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text;
                try {
                    text = stripper.getText(document);
                } catch (IOException e) {
                    throw new RuntimeException("Error extracting text from page " + i + ": " + e.getMessage(), e);
                }
                pages.add(new PageContent(i, text));
            }

            boolean anyNonBlank = pages.stream()
                    .anyMatch(p -> p.getText() != null && !p.getText().isBlank());
            if (!anyNonBlank) {
                throw new IllegalArgumentException("PDF contains no extractable text");
            }

            return pages;
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                log.warn("Error closing PDF document", e);
            }
        }
    }
}
