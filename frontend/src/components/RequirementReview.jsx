import { useState } from "react";

export function RequirementReview({ requirements, mappings, summary, onReview }) {
  const byReq = new Map();
  for (const m of mappings) {
    const list = byReq.get(m.requirement_id) ?? [];
    list.push(m);
    byReq.set(m.requirement_id, list);
  }

  const statusById = new Map();
  for (const item of summary?.requirements ?? []) {
    statusById.set(item.requirement_id, item.status);
  }

  if (requirements.length === 0) return null;

  return (
    <section className="panel">
      <h2 className="panel__title">Requirement Review</h2>
      <div className="req-list">
        {requirements.map((req) => (
          <RequirementCard
            key={req.id}
            requirement={req}
            mappings={byReq.get(req.id) ?? []}
            displayStatus={statusById.get(req.id) ?? "missing"}
            onReview={onReview}
          />
        ))}
      </div>
    </section>
  );
}

function RequirementCard({ requirement, mappings, displayStatus, onReview }) {
  const [open, setOpen] = useState(false);

  return (
    <div className="req-card">
      <button
        className="req-card__header"
        onClick={() => setOpen((o) => !o)}
        aria-expanded={open}
      >
        <span className="req-card__chevron">{open ? "▾" : "▸"}</span>
        <span className="req-card__text">{requirement.text}</span>
        <span className="req-card__badges">
          <span
            className={`badge badge--${requirement.classification === "mandatory" ? "mandatory" : "recommendation"}`}
          >
            {requirement.classification}
          </span>
          <span className={`badge badge--${displayStatus}`}>{displayStatus}</span>
          {requirement.source_page && (
            <span className="badge badge--neutral">p.{requirement.source_page}</span>
          )}
        </span>
      </button>

      {open && (
        <div className="req-card__body">
          {requirement.source_excerpt && (
            <blockquote className="citation">
              <span className="citation__label">Guideline excerpt</span>
              {requirement.source_excerpt}
            </blockquote>
          )}

          {mappings.length === 0 ? (
            <p className="muted">No evidence mapped to this requirement.</p>
          ) : (
            mappings.map((m) => (
              <MappingRow key={m.id} mapping={m} onReview={onReview} />
            ))
          )}
        </div>
      )}
    </div>
  );
}

function MappingRow({ mapping, onReview }) {
  const [correcting, setCorrecting] = useState(false);
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  async function act(action, reviewerNote) {
    setError(null);
    setLoading(true);
    try {
      await onReview(mapping.id, action, reviewerNote);
      setCorrecting(false);
      setNote("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Action failed");
    } finally {
      setLoading(false);
    }
  }

  const isPending = mapping.review_status === "pending";

  return (
    <div className={`mapping-row mapping-row--${mapping.review_status}`} data-testid="mapping-row">
      <div className="mapping-row__meta">
        <span className={`badge badge--confidence-${mapping.ai_confidence}`}>
          {mapping.ai_confidence}
        </span>
        <span className={`badge badge--${mapping.review_status}`}>
          {mapping.review_status}
        </span>
      </div>

      {mapping.ai_citation && (
        <blockquote className="citation">
          <span className="citation__label">Application evidence</span>
          {mapping.ai_citation}
        </blockquote>
      )}

      {mapping.guideline_citation && (
        <blockquote className="citation citation--secondary">
          <span className="citation__label">Guideline</span>
          {mapping.guideline_citation}
        </blockquote>
      )}

      {mapping.ai_explanation && (
        <p className="mapping-row__explanation">{mapping.ai_explanation}</p>
      )}

      {mapping.reviewer_note && (
        <p className="mapping-row__note">
          <strong>Reviewer:</strong> {mapping.reviewer_note}
        </p>
      )}

      {error && <p className="error-text">{error}</p>}

      {isPending && !correcting && (
        <div className="mapping-row__actions">
          <button className="btn btn--success" onClick={() => act("confirm")} disabled={loading}>
            Confirm
          </button>
          <button className="btn btn--warning" onClick={() => setCorrecting(true)} disabled={loading}>
            Correct
          </button>
          <button className="btn btn--danger" onClick={() => act("reject")} disabled={loading}>
            Reject
          </button>
        </div>
      )}

      {correcting && (
        <div className="correction-form">
          <textarea
            className="correction-form__input"
            placeholder="Describe your correction…"
            value={note}
            onChange={(e) => setNote(e.target.value)}
            rows={2}
            aria-label="Correction note"
          />
          <div className="mapping-row__actions">
            <button
              className="btn btn--warning"
              onClick={() => act("correct", note)}
              disabled={loading || !note.trim()}
            >
              Save correction
            </button>
            <button
              className="btn btn--ghost"
              onClick={() => { setCorrecting(false); setNote(""); }}
              disabled={loading}
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
