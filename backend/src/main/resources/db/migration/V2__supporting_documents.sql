-- V2__supporting_documents.sql
-- Track supporting documents expected for an assessment and whether each has been received.

CREATE TABLE supporting_documents (
    id            VARCHAR(32) NOT NULL PRIMARY KEY,
    assessment_id VARCHAR(32) NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    name          TEXT        NOT NULL,
    description   TEXT,
    received      BOOLEAN     NOT NULL DEFAULT FALSE,
    received_at   TIMESTAMP,
    notes         TEXT,
    created_at    TIMESTAMP   NOT NULL
);

CREATE INDEX idx_supporting_docs_assessment_id ON supporting_documents (assessment_id);
