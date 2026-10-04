# AGENT_USAGE.md

This document records how AI/development agents were used while building this project, including representative prompts, delegated work, important mistakes made by the agent, suggestions I rejected, and how I verified the generated output.

---

## Tools used

- **Claude Code (Anthropic)** — primary coding assistant used throughout the project for backend scaffolding, prompt engineering, debugging, and deployment setup.
- **DeepSeek API** — runtime LLM provider for the application's AI workflow (requirement extraction, mapping, gap detection, clarification questions, unsupported-claim detection).
- **IntelliJ IDEA** — IDE for Java/Spring Boot development.
- **GitHub + Render** — version control and deployment.

---

## Work delegated to the agent

- **Backend scaffolding** — Spring Boot project layout, JPA entities, repositories, controllers, DTOs, Flyway migrations.
- **AI workflow design** — prompt wording for each AI step (requirement extraction, evidence extraction, mapping, gap detection, clarification questions, unsupported-claim detection), the `LLMProvider` abstraction, and the mock provider.
- **Frontend components** — React components for document upload, requirement review, summary panel, questions list, claims list, and supporting-document tracking.
- **Deterministic status logic** — `SummaryService.getRequirementStatus()` that aggregates per-requirement status from review state and AI confidence.
- **Deployment** — Dockerfiles, nginx config, Render setup instructions, environment-variable wiring.
- **Documentation** — README structure and content.

I retained decision-making on scope, feature trade-offs, UI structure, and when to accept vs reject the agent's suggestions.

---

## Representative prompts

Examples of the kinds of prompts I used (verbatim or close to it):

- *"Extract requirements, evidence, and classify them from this guideline. Must be grounded in the document text with page citations — do not invent."*
- *"The assessment classification is too conservative. Explicit evidence is being marked ambiguous. Inspect the mapping prompt, response schema, and status calculation, then make the smallest fix."*
- *"The requirement extraction returned only 3 items with hallucinated text. Compare against the source PDF and find the regression — do not hardcode sample answers."*
- *"The UI shows statuses that don't match the summary totals. Find the stale/derived status calculation on the frontend and reconcile them against the backend's deterministic status."*
- *"Implement supporting-document tracking: entity, migration, REST endpoints, and a minimal UI panel."*
- *"Guide me through deploying this on Render step by step. I only run locally; I don't want to over-complicate Docker."*

---

## Important agent mistakes (and how they were caught)

1. **Hardcoded sample data confused for a real LLM result.** Early on, requirement extraction returned only 3 items that were suspiciously generic ("operated for at least two years"). The agent initially suspected an LLM hallucination. The real cause: the mock `LLMProvider` was running instead of DeepSeek because the active Spring profile wasn't activating the real provider. I caught this by diffing the output against the actual guideline PDF.

2. **Status function missed the `strong` case.** The deterministic status function bucketed pending-strong mappings into "ambiguous" because the if/else chain had no explicit branch for `"strong"`. The summary said "8 ambiguous" even though DeepSeek was returning 8 strong matches. I caught this by inspecting the raw AI confidence distribution via the API.

3. **Frontend had a duplicate status function with the same bug.** Even after I fixed the backend logic, the Requirement Review section still showed the wrong statuses because the frontend recomputed status client-side with the same bug. The fix was to make the frontend consume the backend's authoritative status from the summary endpoint — eliminating the duplication.

4. **Hardcoded API key in `application.yml`.** The agent at one point put the DeepSeek key directly in a Spring profile for local-dev convenience. I rejected this and had it moved out to env vars with a `.env.example` placeholder before pushing to a repo.

5. **Env var baked into Vite build at deploy time was missed.** When deploying the frontend to Render, the agent assumed updating `VITE_API_URL` env var would take effect immediately. In reality, Vite bakes env vars into the build at compile time, so a manual redeploy was required. This cost one debugging cycle.

6. **Nginx default 1 MB upload limit silently blocked PDFs.** The agent initially missed that nginx's default `client_max_body_size` is 1 MB. Larger PDFs failed with a generic 413 that looked like an application error. Fixed by setting an explicit `client_max_body_size 25M;`.

---

## Suggestions I rejected

- **Adding `spring-dotenv` dependency to auto-load `.env`.** Rejected because Spring Boot already has first-class profile support (`application-<profile>.yml` + `SPRING_PROFILES_ACTIVE`). Adding a Node-style `.env` loader was unnecessary complexity.
- **Lombok annotations across all entities and DTOs.** Rejected after Lombok 1.18.34 failed to run under Java 21 (`TypeTag::UNKNOWN` via `sun.misc.Unsafe`). All entities were rewritten with explicit getters/setters and explicit `Builder` inner classes for the response DTOs.
- **Backing a `/health` endpoint UI route.** The agent at one point suggested adding a health-page UI. Rejected — `/health` is purely a backend liveness/DB-check endpoint for Render's health checks, not something users ever see.
- **Over-prompting the LLM with few-shot examples copying the sample PDF.** Rejected because that would risk the LLM memorising the sample instead of grounding on arbitrary uploads. Prompts stayed scenario-neutral with explicit "do not invent" rules.
- **Deterministic auto-tick for the "Submit as PDF" requirement** (since the backend already knows the uploaded file is a PDF). Rejected for this iteration — kept the extraction pipeline fully text-grounded; adding deterministic requirement overrides would blur the AI/review boundary.

---

## How I verified the output

- **Unit tests on the frontend** — 26 Vitest tests covering RequirementReview, SummaryPanel, and the API client (passing).
- **End-to-end manual testing with real sample PDFs** — uploaded the provided `sample_grant_guideline.pdf` and `sample_grant_application.pdf` through the full pipeline and compared the extracted requirements/claims/mappings against the actual document text. Verified:
  - 12 requirements extracted (7 mandatory + 2 recommendations + 3 submission statements) — all with verbatim excerpts and page 1 citations.
  - No invented requirements (specifically checked that "operated for at least two years" never appeared).
  - No invented unsupported claims (specifically checked that "most experienced team" never appeared).
  - Summary totals match per-requirement statuses exactly.
  - Clarification questions tie only to weak/missing/ambiguous requirements.
- **Direct API inspection** — called `/api/v1/assessments/{id}/mappings` and `/summary` and reconciled the confidence distribution with the summary buckets manually.
- **Compile/test gates** — `mvn clean compile` and `npm test -- --run` run on every change.
- **Deployment smoke test** — opened the hosted frontend, uploaded both PDFs, ran analysis, and verified every section of the UI reflects the API data.

---

**Rajeev Ranjan | NIT ALLAHABAD**
