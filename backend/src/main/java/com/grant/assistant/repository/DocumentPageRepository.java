package com.grant.assistant.repository;

import com.grant.assistant.model.DocumentPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentPageRepository extends JpaRepository<DocumentPage, String> {

    List<DocumentPage> findByDocumentVersionIdOrderByPageNumberAsc(String documentVersionId);
}
