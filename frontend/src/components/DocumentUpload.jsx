import { useRef, useState } from "react";

export function DocumentUpload({ label, doc, groupId, onUpload, onUploadVersion, disabled }) {
  const inputRef = useRef(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  async function handleFile(file) {
    setError(null);
    setLoading(true);
    try {
      if (groupId) {
        await onUploadVersion(file);
      } else {
        await onUpload(file);
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : "Upload failed");
    } finally {
      setLoading(false);
      if (inputRef.current) inputRef.current.value = "";
    }
  }

  function handleChange(e) {
    const file = e.target.files?.[0];
    if (file) handleFile(file);
  }

  function handleDrop(e) {
    e.preventDefault();
    const file = e.dataTransfer.files?.[0];
    if (file) handleFile(file);
  }

  return (
    <div className="doc-upload">
      <div className="doc-upload__header">
        <span className="doc-upload__label">{label}</span>
        {doc && (
          <span className="badge badge--neutral">
            v{doc.version_number} · {doc.page_count ?? "?"} pages
          </span>
        )}
      </div>

      {doc && (
        <div className="doc-upload__current">
          <span className="doc-upload__filename">{doc.filename}</span>
        </div>
      )}

      <div
        className={`drop-zone${loading ? " drop-zone--loading" : ""}`}
        onDragOver={(e) => e.preventDefault()}
        onDrop={handleDrop}
        onClick={() => !disabled && !loading && inputRef.current?.click()}
        role="button"
        tabIndex={disabled || loading ? -1 : 0}
        onKeyDown={(e) =>
          e.key === "Enter" && !disabled && !loading && inputRef.current?.click()
        }
        aria-label={`Upload ${label} PDF`}
      >
        <input
          ref={inputRef}
          type="file"
          accept=".pdf"
          style={{ display: "none" }}
          onChange={handleChange}
          disabled={disabled || loading}
        />
        {loading ? (
          <span className="spinner" aria-label="Uploading…" />
        ) : (
          <span>
            {doc ? "Drop to replace · click to browse" : "Drop PDF here · click to browse"}
          </span>
        )}
      </div>

      {error && <p className="error-text">{error}</p>}
    </div>
  );
}
