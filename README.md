# Grant Application Completeness Assistant

A web application that reviews a draft grant application against a funder's guideline document. It extracts requirements, maps application evidence to each requirement with citations, and produces a reviewed completeness summary that the user can confirm, correct, or reject.

The tool is an assistant for completeness review only — it does not make funding-eligibility decisions.

---

## Setup

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 22+
- (Optional) PostgreSQL 16+ — the app runs on H2 by default
- (Optional) OpenAI-compatible LLM API key (DeepSeek, Groq, etc.) — a built-in mock provider works without one

### Backend

```bash
cd backend
mvn spring-boot:run
```

Starts on `http://localhost:8080`. Flyway migrations run on startup. Default DB is a file-based H2 (`backend/dev.mv.db`) so no external DB is needed to try the app. Health check: `GET /health`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Starts on `http://localhost:5173`. Vite proxies `/api/*` to the backend on port 8080.

### Configuration

All settings read from environment variables via `application.yml` with sensible defaults. Copy `.env.example` to understand the available variables. Never commit real secrets.

For local development with a real LLM, create a gitignored `backend/src/main/resources/application-local.yml` with your `app.llm.*` values, or export them as env vars before running `mvn spring-boot:run`.

---

## Architecture

**Backend** — Java 21 + Spring Boot 3.3.5 (Web, Data JPA) + Maven + Flyway. Entities and services are split by domain (`model/`, `repository/`, `service/`, `controller/`, `dto/`). PDF text extraction uses Apache PDFBox 3.0.3. The AI workflow lives in `service/ai/` behind an `LLMProvider` interface with two implementations: `OpenAICompatProvider` (any OpenAI-compatible HTTP API) and `MockProvider` (deterministic responses for development/testing).

**Frontend** — React 19 + Vite 8 + plain JavaScript. Single-page app with components for document upload, requirement review, summary panel, questions, claims, and supporting-document tracking. State is managed in `App.jsx`; the API client in `api/client.js` reads its base URL from `VITE_API_URL`.

**Database** — H2 (file-based) for local dev, PostgreSQL for production. Schema is managed entirely by Flyway migrations in `backend/src/main/resources/db/migration/`.

**Completeness logic is deterministic** — the backend aggregates per-requirement status from the latest mapping and review state (`SummaryService`). The LLM never computes the final percentage.

### Project structure

```
project/
├── backend/
│   ├── src/main/java/com/grant/assistant/
│   │   ├── config/        # Spring configuration (CORS, AI wiring)
│   │   ├── controller/    # REST endpoints
│   │   ├── dto/           # request/response DTOs
│   │   ├── exception/     # global error handling
│   │   ├── model/         # JPA entities
│   │   ├── processing/    # PDF text extraction
│   │   ├── repository/    # Spring Data repositories
│   │   └── service/       # business + AI workflow
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/  # Flyway migrations
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/           # fetch client
│   │   ├── components/    # React components
│   │   └── tests/         # Vitest tests
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── .env.example
├── AGENT_USAGE.md
└── README.md
```

---

## Completed Scope

- PDF ingestion with per-page extraction and page-level citations
- LLM-driven requirement extraction, classified as mandatory or recommendation
- Evidence extraction from the application
- Requirement-to-evidence mapping with confidence levels: `strong`, `weak`, `ambiguous`, `none`
- Verbatim guideline + application citations with page numbers on every mapping
- Clarification questions generated for weak/missing requirements
- Unsupported-claim detection grounded in the application text
- Human review workflow: confirm, correct, or reject each AI mapping
- Deterministic completeness summary computed in the backend
- Guideline and application document versioning
- Stale-assessment detection when a newer version of either document is uploaded
- Supporting-document tracking (add expected documents, mark received, remove)
- LLM provider abstraction with OpenAI-compatible and mock implementations
- Structured logs; API keys are never logged
- Dockerfiles for both services + ready for deployment on container platforms

---

## Excluded Scope

- No OCR — scanned/image-only PDFs will produce empty extracted text
- No external grant-database search
- No automated application submission
- No financial forecasting
- No automatic document writing or AI-generated application copy
- No authoritative legal or funding-eligibility decisions

---

## Tests

**Frontend** — 26 tests covering components (requirement review, summary panel) and the API client. All passing.

```bash
cd frontend
npm test
```

**Backend** — no automated tests yet. `mvn test` runs successfully with "no tests to run". End-to-end verification was done manually with sample PDFs via the REST API.

---

## Deployment

Both services are containerised with standalone Dockerfiles and deployed on Render:

- **Frontend** (Static Site) — Vite build served over Render's CDN. Base API URL is injected at build time via `VITE_API_URL`.
- **Backend** (Web Service, Docker) — Spring Boot JAR in an Alpine JRE image. Listens on `$PORT` (Render-provided).
- **Database** — Render-managed PostgreSQL (free tier). Flyway migrations auto-apply on startup.

All credentials (DB password, LLM API key) are set as Render environment variables — no secrets live in source. The free tier backend sleeps after 15 minutes idle; the first request after sleep takes ~30–60s to wake it.

### Hosted URLs

- Frontend: `https://grant-frontend-c9ty.onrender.com`
- Backend health: `https://grant-application-completeness-assistant-v3z7.onrender.com/health`

### Local Docker (optional)

```bash
cd backend  && docker build -t grant-backend  . && docker run -p 8080:8080 grant-backend
cd frontend && docker build -t grant-frontend . && docker run -p 3000:80  grant-frontend
```

---

## Limitations

- Free-tier backend sleeps when idle; first request after sleep is slow.
- No OCR means scanned PDFs are not supported.
- Single-user application — no authentication or multi-tenant isolation.
- Supporting documents are tracked by name only; file contents are not verified.
- Submission-format requirements (e.g. "application should be submitted as a PDF") are evaluated from the application's own text and may be marked missing even when the submitted file is a PDF.
- No automated backend tests yet; coverage is manual + frontend-only.

---

## Screenshots

<img width="1688" height="946" alt="Screenshot 1" src="https://github.com/user-attachments/assets/acc952c3-c374-4116-ac5f-9145cf952794" />
<img width="1225" height="946" alt="Screenshot 2" src="https://github.com/user-attachments/assets/2f0ac4f0-010a-4ec0-ba79-e980efaea034" />
<img width="1017" height="769" alt="Screenshot 3" src="https://github.com/user-attachments/assets/d20e458b-c42a-4a38-9a1d-d6933d061042" />

---

**Rajeev Ranjan | NIT ALLAHABAD**
