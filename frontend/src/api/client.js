export class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

const API_BASE = import.meta.env.VITE_API_URL ?? "/api";

async function request(path, options) {
  let res;
  try {
    res = await fetch(`${API_BASE}${path}`, options);
  } catch {
    throw new ApiError(0, "Network error — is the backend running?");
  }
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new ApiError(res.status, body.detail ?? res.statusText);
  }
  return res.json();
}

function uploadFile(path, file) {
  const form = new FormData();
  form.append("file", file);
  return request(path, { method: "POST", body: form });
}

export const api = {
  uploadGuideline: (file) => uploadFile("/v1/documents/guideline", file),

  uploadApplication: (file) => uploadFile("/v1/documents/application", file),

  uploadGuidelineVersion: (groupId, file) =>
    uploadFile(`/v1/documents/guideline/${groupId}/version`, file),

  uploadApplicationVersion: (groupId, file) =>
    uploadFile(`/v1/documents/application/${groupId}/version`, file),

  createAssessment: (guidelineVersionId, applicationVersionId) =>
    request("/v1/assessments", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        guideline_version_id: guidelineVersionId,
        application_version_id: applicationVersionId,
      }),
    }),

  getAssessment: (id) => request(`/v1/assessments/${id}`),

  analyzeAssessment: (id) =>
    request(`/v1/assessments/${id}/analyze`, { method: "POST" }),

  getSummary: (id) => request(`/v1/assessments/${id}/summary`),

  getRequirements: (id) => request(`/v1/assessments/${id}/requirements`),

  getMappings: (id) => request(`/v1/assessments/${id}/mappings`),

  getQuestions: (id) => request(`/v1/assessments/${id}/questions`),

  getClaims: (id) => request(`/v1/assessments/${id}/claims`),

  patchMapping: (id, action, reviewerNote) =>
    request(`/v1/mappings/${id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ action, reviewer_note: reviewerNote ?? null }),
    }),

  listSupportingDocuments: (assessmentId) =>
    request(`/v1/assessments/${assessmentId}/supporting-documents`),

  createSupportingDocument: (assessmentId, name, description) =>
    request(`/v1/assessments/${assessmentId}/supporting-documents`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, description: description ?? null }),
    }),

  updateSupportingDocument: (docId, fields) =>
    request(`/v1/supporting-documents/${docId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(fields),
    }),

  deleteSupportingDocument: async (docId) => {
    const res = await fetch(`${API_BASE}/v1/supporting-documents/${docId}`, { method: "DELETE" });
    if (!res.ok) throw new ApiError(res.status, res.statusText);
  },
};
