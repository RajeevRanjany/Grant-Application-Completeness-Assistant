package com.grant.assistant.repository;

import com.grant.assistant.model.UnsupportedClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnsupportedClaimRepository extends JpaRepository<UnsupportedClaim, String> {

    List<UnsupportedClaim> findByAssessmentId(String assessmentId);
}
