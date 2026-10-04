package com.grant.assistant.repository;

import com.grant.assistant.model.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, String> {

    @Query("SELECT MAX(dv.versionNumber) FROM DocumentVersion dv WHERE dv.documentGroupId = :groupId")
    Optional<Integer> findMaxVersionNumberByGroupId(@Param("groupId") String groupId);

    boolean existsByDocumentGroupId(String documentGroupId);
}
