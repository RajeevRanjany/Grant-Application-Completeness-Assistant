import { useCallback, useState } from "react";
import { api, ApiError } from "./api/client.js";
import { ClaimsList } from "./components/ClaimsList.jsx";
import { DocumentUpload } from "./components/DocumentUpload.jsx";
import { QuestionsList } from "./components/QuestionsList.jsx";
import { RequirementReview } from "./components/RequirementReview.jsx";
import { SummaryPanel } from "./components/SummaryPanel.jsx";
import { SupportingDocuments } from "./components/SupportingDocuments.jsx";
import "./App.css";

function derivePhase(state) {
  if (!state.guideline || !state.application) return "documents";
  if (!state.assessment) return "create";
  if (state.assessment.status !== "ready" && state.assessment.status !== "failed")
    return "analyze";
  return "results";
}

export default function App() {
  const [state, setState] = useState({
    guideline: null,
    application: null,
    assessment: null,
    summary: null,
    requirements: [],
    mappings: [],
    questions: [],
    claims: [],
    supportingDocuments: [],
  });

  const [busy, setBusy] = useState(false);
  const [globalError, setGlobalError] = useState(null);

  const phase = derivePhase(state);

  function patch(partial) {
    setState((s) => ({ ...s, ...partial }));
  }

  async function run(fn) {
    setBusy(true);
    setGlobalError(null);
    try {
      return await fn();
    } catch (e) {
      setGlobalError(e instanceof ApiError ? e.message : "An unexpected error occurred");
      return undefined;
    } finally {
      setBusy(false);
    }
  }

  async function handleUploadGuideline(file) {
    const doc = await run(() => api.uploadGuideline(file));
    if (doc)
      patch({
        guideline: doc,
        assessment: null,
        summary: null,
        requirements: [],
        mappings: [],
        questions: [],
        claims: [],
        supportingDocuments: [],
      });
  }

  async function handleUploadGuidelineVersion(file) {
    if (!state.guideline) return;
    const doc = await run(() =>
      api.uploadGuidelineVersion(state.guideline.document_group_id, file),
    );
    if (doc) patch({ guideline: doc });
  }

  async function handleUploadApplication(file) {
    const doc = await run(() => api.uploadApplication(file));
    if (doc)
      patch({
        application: doc,
        assessment: null,
        summary: null,
        requirements: [],
        mappings: [],
        questions: [],
        claims: [],
        supportingDocuments: [],
      });
  }

  async function handleUploadApplicationVersion(file) {
    if (!state.application) return;
    const doc = await run(() =>
      api.uploadApplicationVersion(state.application.document_group_id, file),
    );
    if (doc) patch({ application: doc });
  }

  async function handleCreateAssessment() {
    if (!state.guideline || !state.application) return;
    const a = await run(() =>
      api.createAssessment(state.guideline.id, state.application.id),
    );
    if (a) patch({ assessment: a, supportingDocuments: [] });
  }

  async function handleAddSupportingDoc(name, description) {
    if (!state.assessment) return;
    const created = await api.createSupportingDocument(state.assessment.id, name, description);
    setState((s) => ({ ...s, supportingDocuments: [...s.supportingDocuments, created] }));
  }

  async function handleToggleSupportingDocReceived(docId, received) {
    const updated = await api.updateSupportingDocument(docId, { received });
    setState((s) => ({
      ...s,
      supportingDocuments: s.supportingDocuments.map((d) => (d.id === docId ? updated : d)),
    }));
  }

  async function handleDeleteSupportingDoc(docId) {
    await api.deleteSupportingDocument(docId);
    setState((s) => ({
      ...s,
      supportingDocuments: s.supportingDocuments.filter((d) => d.id !== docId),
    }));
  }

  async function handleAnalyze() {
    if (!state.assessment) return;
    const a = await run(() => api.analyzeAssessment(state.assessment.id));
    if (!a) return;
    patch({ assessment: a });

    if (a.status === "ready") {
      setBusy(true);
      try {
        const [summary, requirements, mappings, questions, claims, supportingDocuments] =
          await Promise.all([
            api.getSummary(a.id),
            api.getRequirements(a.id),
            api.getMappings(a.id),
            api.getQuestions(a.id),
            api.getClaims(a.id),
            api.listSupportingDocuments(a.id),
          ]);
        patch({ summary, requirements, mappings, questions, claims, supportingDocuments });
      } catch (e) {
        setGlobalError(e instanceof ApiError ? e.message : "Failed to load results");
      } finally {
        setBusy(false);
      }
    }
  }

  const handleReview = useCallback(
    async (mappingId, action, note) => {
      const updated = await api.patchMapping(mappingId, action, note);
      setState((s) => ({
        ...s,
        mappings: s.mappings.map((m) => (m.id === updated.id ? updated : m)),
      }));
      if (state.assessment?.id) {
        try {
          const summary = await api.getSummary(state.assessment.id);
          setState((s) => ({ ...s, summary }));
        } catch {
          // best-effort
        }
      }
    },
    [state.assessment?.id],
  );

  return (
    <div className="app">
      <header className="app-header">
        <h1 className="app-header__title">Grant Application Completeness Assistant</h1>
        <p className="app-header__subtitle">
          Review your application against funder requirements
        </p>
      </header>

      <main className="app-main">
        {globalError && (
          <div className="alert alert--error" role="alert">
            {globalError}
            <button
              className="alert__dismiss"
              onClick={() => setGlobalError(null)}
              aria-label="Dismiss error"
            >
              ✕
            </button>
          </div>
        )}

        {state.assessment?.is_stale && (
          <div className="alert alert--stale" role="status">
            <strong>Stale assessment:</strong> A newer document version has been
            uploaded. Re-run analysis to reflect the latest content.
          </div>
        )}

        <section className="panel">
          <h2 className="panel__title">Documents</h2>
          <div className="doc-row">
            <DocumentUpload
              label="Grant Guideline"
              doc={state.guideline}
              groupId={state.guideline?.document_group_id ?? null}
              onUpload={handleUploadGuideline}
              onUploadVersion={handleUploadGuidelineVersion}
              disabled={busy}
            />
            <DocumentUpload
              label="Application Draft"
              doc={state.application}
              groupId={state.application?.document_group_id ?? null}
              onUpload={handleUploadApplication}
              onUploadVersion={handleUploadApplicationVersion}
              disabled={busy}
            />
          </div>
        </section>

        {phase !== "documents" && (
          <section className="panel panel--actions">
            {phase === "create" && (
              <div className="action-row">
                <p className="muted" style={{ margin: 0 }}>
                  Both documents uploaded. Ready to create an assessment.
                </p>
                <button
                  className="btn btn--primary"
                  onClick={handleCreateAssessment}
                  disabled={busy}
                >
                  {busy && <span className="spinner spinner--inline" />}
                  Create Assessment
                </button>
              </div>
            )}

            {(phase === "analyze" || phase === "results") && (
              <div className="action-row">
                <div className="action-row__info">
                  <span className="muted">Assessment</span>
                  <code className="id-chip">{state.assessment?.id.slice(0, 8)}…</code>
                  <span className={`badge badge--status-${state.assessment?.status}`}>
                    {state.assessment?.status}
                  </span>
                </div>
                <button
                  className="btn btn--primary"
                  onClick={handleAnalyze}
                  disabled={busy || state.assessment?.status === "analyzing"}
                >
                  {busy && <span className="spinner spinner--inline" />}
                  {phase === "results" ? "Re-analyze" : "Run Analysis"}
                </button>
              </div>
            )}

            {state.assessment?.status === "failed" &&
              state.assessment.failure_reason && (
                <div
                  className="alert alert--error"
                  style={{ marginTop: "0.75rem" }}
                >
                  <strong>Analysis failed:</strong>{" "}
                  {state.assessment.failure_reason}
                </div>
              )}
          </section>
        )}

        {state.assessment && (
          <SupportingDocuments
            docs={state.supportingDocuments}
            onAdd={handleAddSupportingDoc}
            onToggleReceived={handleToggleSupportingDocReceived}
            onDelete={handleDeleteSupportingDoc}
            disabled={busy}
          />
        )}

        {state.summary && <SummaryPanel summary={state.summary} />}

        {state.requirements.length > 0 && (
          <RequirementReview
            requirements={state.requirements}
            mappings={state.mappings}
            summary={state.summary}
            onReview={handleReview}
          />
        )}

        {state.questions.length > 0 && (
          <QuestionsList
            questions={state.questions}
            requirements={state.requirements}
          />
        )}

        {state.claims.length > 0 && <ClaimsList claims={state.claims} />}
      </main>
    </div>
  );
}
