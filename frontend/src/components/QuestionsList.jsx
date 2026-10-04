export function QuestionsList({ questions, requirements }) {
  if (questions.length === 0) return null;

  const reqById = new Map(requirements.map((r) => [r.id, r]));

  return (
    <section className="panel">
      <h2 className="panel__title">Clarification Questions</h2>
      <p className="panel__subtitle">
        These questions should be addressed in your application to strengthen weak or ambiguous evidence.
      </p>
      <ol className="questions-list">
        {questions.map((q) => {
          const req = reqById.get(q.requirement_id);
          return (
            <li key={q.id} className="questions-list__item">
              <p className="questions-list__text">{q.question_text}</p>
              {req && (
                <p className="muted questions-list__ref">Re: {req.text}</p>
              )}
            </li>
          );
        })}
      </ol>
    </section>
  );
}
