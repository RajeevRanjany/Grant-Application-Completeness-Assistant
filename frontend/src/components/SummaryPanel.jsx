export function SummaryPanel({ summary }) {
  const { total_requirements, satisfied, weak, ambiguous, missing, completion_pct } = summary;

  return (
    <section className="panel" data-testid="summary-panel">
      <h2 className="panel__title">Completeness Summary</h2>

      <div className="summary-bar-wrap">
        <div className="summary-bar">
          <div
            className="summary-bar__fill"
            style={{ width: `${completion_pct}%` }}
            role="progressbar"
            aria-valuenow={completion_pct}
            aria-valuemin={0}
            aria-valuemax={100}
          />
        </div>
        <span className="summary-bar__pct">{completion_pct}%</span>
      </div>

      <div className="summary-stats">
        <Stat label="Total" value={total_requirements} />
        <Stat label="Satisfied" value={satisfied} variant="satisfied" />
        <Stat label="Weak" value={weak} variant="weak" />
        <Stat label="Ambiguous" value={ambiguous} variant="ambiguous" />
        <Stat label="Missing" value={missing} variant="missing" />
      </div>
    </section>
  );
}

function Stat({ label, value, variant }) {
  return (
    <div className={`stat${variant ? ` stat--${variant}` : ""}`}>
      <span className="stat__value">{value}</span>
      <span className="stat__label">{label}</span>
    </div>
  );
}
