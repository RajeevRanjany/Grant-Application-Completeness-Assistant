-- V1__init.sql
-- Initial schema for Grant Assistant
-- Uses VARCHAR(32) for UUID hex IDs (matching Python uuid.hex format)
-- Compatible with both H2 (local dev) and PostgreSQL (production)

CREATE TABLE document_versions (
    id                VARCHAR(32)  NOT NULL PRIMARY KEY,
    document_type     VARCHAR(20)  NOT NULL,
    filename          TEXT         NOT NULL,
    file_path         TEXT         NOT NULL,
    version_number    INT          NOT NULL,
    document_group_id VARCHAR(32)  NOT NULL,
    extracted_text    TEXT,
    page_count        INT,
    is_empty          BOOLEAN      NOT NULL DEFAULT FALSE,
    uploaded_at       TIMESTAMP    NOT NULL
);

CREATE INDEX idx_doc_versions_group_id ON document_versions (document_group_id);

CREATE TABLE document_pages (
    id                  VARCHAR(32) NOT NULL PRIMARY KEY,
    document_version_id VARCHAR(32) NOT NULL REFERENCES document_versions(id) ON DELETE CASCADE,
    page_number         INT         NOT NULL,
    text                TEXT        NOT NULL
);

CREATE INDEX idx_doc_pages_version_id ON document_pages (document_version_id);

CREATE TABLE assessments (
    id                     VARCHAR(32) NOT NULL PRIMARY KEY,
    guideline_version_id   VARCHAR(32) NOT NULL REFERENCES document_versions(id),
    application_version_id VARCHAR(32) NOT NULL REFERENCES document_versions(id),
    status                 VARCHAR(20) NOT NULL DEFAULT 'pending',
    analyzed_at            TIMESTAMP,
    failure_reason         TEXT,
    created_at             TIMESTAMP   NOT NULL
);

CREATE INDEX idx_assessments_guideline_id   ON assessments (guideline_version_id);
CREATE INDEX idx_assessments_application_id ON assessments (application_version_id);

CREATE TABLE requirements (
    id             VARCHAR(32)  NOT NULL PRIMARY KEY,
    assessment_id  VARCHAR(32)  NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    text           TEXT         NOT NULL,
    classification VARCHAR(20)  NOT NULL,
    source_page    INT,
    source_excerpt TEXT,
    order_index    INT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_requirements_assessment_id ON requirements (assessment_id);

CREATE TABLE evidence_items (
    id             VARCHAR(32) NOT NULL PRIMARY KEY,
    assessment_id  VARCHAR(32) NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    text           TEXT        NOT NULL,
    source_page    INT,
    source_excerpt TEXT,
    category       TEXT,
    order_index    INT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_evidence_items_assessment_id ON evidence_items (assessment_id);

CREATE TABLE requirement_mappings (
    id               VARCHAR(32) NOT NULL PRIMARY KEY,
    requirement_id   VARCHAR(32) NOT NULL REFERENCES requirements(id) ON DELETE CASCADE,
    evidence_item_id VARCHAR(32) REFERENCES evidence_items(id),
    ai_confidence    VARCHAR(20) NOT NULL DEFAULT 'none',
    ai_explanation   TEXT,
    guideline_citation TEXT,
    ai_citation      TEXT,
    review_status    VARCHAR(20) NOT NULL DEFAULT 'pending',
    reviewer_note    TEXT,
    reviewed_at      TIMESTAMP
);

CREATE INDEX idx_req_mappings_requirement_id   ON requirement_mappings (requirement_id);
CREATE INDEX idx_req_mappings_evidence_item_id ON requirement_mappings (evidence_item_id);

CREATE TABLE clarification_questions (
    id             VARCHAR(32) NOT NULL PRIMARY KEY,
    assessment_id  VARCHAR(32) NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    requirement_id VARCHAR(32) NOT NULL REFERENCES requirements(id) ON DELETE CASCADE,
    question_text  TEXT        NOT NULL,
    resolved       BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_clar_questions_assessment_id ON clarification_questions (assessment_id);

CREATE TABLE unsupported_claims (
    id            VARCHAR(32) NOT NULL PRIMARY KEY,
    assessment_id VARCHAR(32) NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    claim_text    TEXT        NOT NULL,
    source_page   INT,
    dismissed     BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_unsupported_claims_assessment_id ON unsupported_claims (assessment_id);
