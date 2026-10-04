package com.grant.assistant.repository;

import com.grant.assistant.model.RequirementMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementMappingRepository extends JpaRepository<RequirementMapping, String> {

    List<RequirementMapping> findByRequirementId(String requirementId);
}
