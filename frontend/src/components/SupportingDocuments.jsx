import { useState } from "react";

export function SupportingDocuments({ docs, onAdd, onToggleReceived, onDelete, disabled }) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [adding, setAdding] = useState(false);
  const [error, setError] = useState(null);

  async function submit(e) {
    e.preventDefault();
    if (!name.trim()) return;
    setError(null);
    setAdding(true);
    try {
      await onAdd(name.trim(), description.trim() || null);
      setName("");
      setDescription("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to add");
    } finally {
      setAdding(false);
    }
  }

  const missingCount = docs.filter((d) => !d.received).length;

  return (
    <section className="panel">
      <h2 className="panel__title">
        Supporting Documents
        {docs.length > 0 && (
          <span className={`badge badge--${missingCount > 0 ? "missing" : "satisfied"}`} style={{ marginLeft: "0.5rem" }}>
            {missingCount > 0 ? `${missingCount} missing` : "all received"}
          </span>
        )}
      </h2>

      <form className="supporting-form" onSubmit={submit}>
        <input
          type="text"
          placeholder="Document name (e.g. Registration Certificate)"
          value={name}
          onChange={(e) => setName(e.target.value)}
          disabled={disabled || adding}
          aria-label="Supporting document name"
        />
        <input
          type="text"
          placeholder="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          disabled={disabled || adding}
          aria-label="Supporting document description"
        />
        <button type="submit" className="btn btn--primary" disabled={disabled || adding || !name.trim()}>
          Add
        </button>
      </form>
      {error && <p className="error-text">{error}</p>}

      {docs.length === 0 ? (
        <p className="muted">No supporting documents tracked yet.</p>
      ) : (
        <ul className="supporting-list">
          {docs.map((d) => (
            <li key={d.id} className={`supporting-item ${d.received ? "supporting-item--received" : "supporting-item--missing"}`}>
              <label className="supporting-item__check">
                <input
                  type="checkbox"
                  checked={d.received}
                  onChange={(e) => onToggleReceived(d.id, e.target.checked)}
                  disabled={disabled}
                />
                <div className="supporting-item__text">
                  <div className="supporting-item__name">{d.name}</div>
                  {d.description && <div className="supporting-item__desc muted">{d.description}</div>}
                </div>
              </label>
              <span className={`badge badge--${d.received ? "satisfied" : "missing"}`}>
                {d.received ? "received" : "missing"}
              </span>
              <button
                className="btn btn--ghost btn--small"
                onClick={() => onDelete(d.id)}
                disabled={disabled}
                aria-label={`Remove ${d.name}`}
              >
                ✕
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
