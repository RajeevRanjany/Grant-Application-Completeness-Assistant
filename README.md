# Grant Application Completeness Assistant

## 1. What is this project?

A web application that helps reviewers check a draft grant application against a funder's guideline document. The user uploads two PDFs — the grant gu

ideline and the draft application — and the system uses an LLM-driven workflow to extract requirements from the guideline, map evidence from the application to each requirement, and summarise what is satisfied, weak, ambiguous, or missing.

Every extracted requirement and every mapped piece of evidence carries a verbatim excerpt and the page it came from. The reviewer can confirm, correct, or reject each AI mapping; those decisions feed into a deterministic checklist-completion summary computed in the backend (not by the LLM). The application also tracks expected supporting documents separately, so reviewers can mark proof-of-registration, letters, etc. as received or missing.

The tool is positioned as an assistant for completeness review only — it does not make funding-eligibility decisions.

## 2. Features:-

- PDF ingestion with per-page text extraction (PDFBox)
- LLM-driven requirement extraction (mandatory vs recommendation)
- Evidence extraction from the draft application
- Requirement-to-evidence mapping with confidence (`strong`, `weak`, `ambiguous`, `none`)
- Verbatim guideline + application citations with page numbers for every mapping
- Clarification questions generated for weak/missing requirements
- Unsupported-claim detection grounded in the application text
- Human review: confirm, correct, or reject each mapping
- Deterministic completeness summary computed in the backend
- Document versioning for both guideline and application
- Stale-assessment detection when a newer version is uploaded
- Supporting-document tracking (add expected docs, mark received, remove)
- LLM provider abstraction: OpenAI-compatible API or deterministic mock
- Structured application logs; API keys are never logged

## 3. Tech Stack:-

**Backend:-**
- Java 21
- Spring Boot 3.3.5 (Web, Data JPA)
- Maven
- PostgreSQL (production), H2 (local file-based default)
- Flyway for schema migrations
- Apache PDFBox 3.0.3
- Jackson
**Frontend:-**
- React 19
- Vite 8
- Plain JavaScript (no TypeScript)
- Vitest + Testing Library + jsdom
- nginx (production container)

**AI:-**
- OpenAI-compatible provider abstraction (works with DeepSeek, Groq, etc.)
- Built-in deterministic `mock` provider for development

## 4. How to Run Locally

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 22+
- PostgreSQL
- An API key for an OpenAI-compatible LLM provider

### How to Clone:-

```bash
git clone https://github.com/RajeevRanjany/Grant-Application-Completeness-Assistant
cd Grant-Application-Completeness-Assistant
```

### Backend:-

```bash
cd backend
mvn spring-boot:run
```

The backend starts on `http://localhost:8080`. On first run, Flyway applies all migrations under `src/main/resources/db/migration/`. By default it uses a file-based H2 database (`backend/dev.mv.db`), so no external database is required to try the app.

Health check: `GET http://localhost:8080/health`.

### Frontend:-

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server starts (default `http://localhost:5173`) and proxies `/api/*` requests to the backend on port 8080.

### Database:-

- **Default (no setup needed):** H2 file database at `backend/dev.mv.db`. Flyway migrations run automatically on startup.
- **PostgreSQL (optional):** set the environment variables below before starting the backend. Flyway will migrate the Postgres schema automatically.

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/grant_assistant
export DB_USERNAME=grant
export DB_PASSWORD=your_database_password
export DB_DRIVER=org.postgresql.Driver
```

### Environment Variables:-

Configuration is driven through Spring Boot's `application.yml`, which reads values from environment variables with sensible defaults for local development.

| Variable | Purpose | Default |
|---|---|---|
| `DATABASE_URL` | JDBC URL | H2 file DB |
| `DB_USERNAME` / `DB_PASSWORD` / `DB_DRIVER` | DB credentials/driver | H2 defaults |
| `LLM_PROVIDER` | `openai_compat` | depends on active Spring profile |
| `LLM_BASE_URL` | LLM API base URL | — |
| `LLM_API_KEY` | LLM API key | — |
| `LLM_MODEL` | Model name | — |
| `UPLOAD_DIR` | Upload storage path | `./uploads` |
| `MAX_UPLOAD_MB` | Max PDF upload size | `20` |

For local development, create a `.env.example` file with placeholder names (no secrets) and share your real values privately. The `.env` file itself must never be committed. API keys, DB passwords, and tokens must only come from environment variables.

## 5. Project Structure:-

```
project/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/grant/assistant/
│   │   │   │   ├── config/         
│   │   │   │   ├── controller/     
│   │   │   │   ├── dto/            
│   │   │   │   ├── exception/      
│   │   │   │   ├── model/          
│   │   │   │   ├── processing/     
│   │   │   │   ├── repository/     
│   │   │   │   └── service/        
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/                   
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api/                   
│   │   ├── components/             
│   │   ├── tests/                  
│   │   ├── App.jsx
│   │   └── main.jsx                
│   ├── vite.config.js
│   └── package.json
│
├── uploads/                        
└── README.md
```

## 6. Application Flow:-

1. **Upload grant guideline** PDF.
2. **Upload application draft** PDF.
3. **Create assessment** linking the two document versions.
4. **Run analysis** — backend extracts requirements from the guideline, extracts evidence from the application, maps evidence to requirements, detects gaps, generates clarification questions, and flags unsupported claims. Every item carries a page citation.
5. **Review requirements** — each requirement is shown with status (`satisfied` / `weak` / `ambiguous` / `missing`), AI confidence, citations, and the AI's explanation. The reviewer can confirm, correct, or reject each mapping.
6. **Track supporting documents** — add expected documents (e.g. registration certificate, authorised-rep letter) and mark each as received.
7. **View the deterministic completeness summary** — totals by status plus a completion percentage, recomputed from the latest mapping/review state.

## Screenshots:-
<img width="1688" height="946" alt="Screenshot 2026-10-05 at 2 46 37 AM" src="https://github.com/user-attachments/assets/acc952c3-c374-4116-ac5f-9145cf952794" />
<img width="1225" height="946" alt="Screenshot 2026-10-05 at 2 47 00 AM" src="https://github.com/user-attachments/assets/2f0ac4f0-010a-4ec0-ba79-e980efaea034" />
<img width="1017" height="769" alt="Screenshot 2026-10-05 at 2 47 13 AM" src="https://github.com/user-attachments/assets/d20e458b-c42a-4a38-9a1d-d6933d061042" />




## 8. Author:-
**Rajeev Ranjan | NIT ALLAHABAD**
