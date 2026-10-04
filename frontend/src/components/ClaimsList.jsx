export function ClaimsList({ claims }) {
  if (claims.length === 0) return null;

  return (
    <section className="panel">
      <h2 className="panel__title">Unsupported Claims</h2>
      <p className="panel__subtitle">
        These assertions in the application are not backed by evidence in the document.
      </p>
      <ul className="claims-list">
        {claims.map((c) => (
          <li key={c.id} className="claims-list__item">
            <blockquote className="citation citation--claim">
              <span className="citation__label">
                Claim{c.source_page ? ` (p.${c.source_page})` : ""}
              </span>
              {c.claim_text}
            </blockquote>
          </li>
        ))}
      </ul>
    </section>
  );
}
