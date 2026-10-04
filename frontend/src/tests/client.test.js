import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, api } from "../api/client.js";

const mockFetch = vi.fn();
beforeEach(() => {
  vi.stubGlobal("fetch", mockFetch);
});
afterEach(() => {
  vi.restoreAllMocks();
});

function ok(data) {
  return {
    ok: true,
    status: 200,
    json: async () => data,
  };
}

function err(status, detail) {
  return {
    ok: false,
    status,
    statusText: "Error",
    json: async () => ({ detail }),
  };
}

describe("api.getAssessment", () => {
  it("returns parsed JSON on 200", async () => {
    const data = { id: "abc", status: "pending" };
    mockFetch.mockResolvedValueOnce(ok(data));
    const result = await api.getAssessment("abc");
    expect(result).toEqual(data);
  });

  it("throws ApiError on 404", async () => {
    mockFetch.mockResolvedValueOnce(err(404, "Assessment not found"));
    await expect(api.getAssessment("missing")).rejects.toMatchObject({
      status: 404,
      message: "Assessment not found",
    });
  });

  it("throws ApiError with status 0 on network failure", async () => {
    mockFetch.mockRejectedValueOnce(new TypeError("Failed to fetch"));
    await expect(api.getAssessment("x")).rejects.toMatchObject({ status: 0 });
  });
});

describe("api.patchMapping", () => {
  it("sends correct JSON body for confirm", async () => {
    const mapping = { id: "m1", review_status: "confirmed" };
    mockFetch.mockResolvedValueOnce(ok(mapping));
    await api.patchMapping("m1", "confirm");
    const body = JSON.parse(mockFetch.mock.calls[0][1].body);
    expect(body.action).toBe("confirm");
    expect(body.reviewer_note).toBeNull();
  });

  it("sends reviewer_note for correct action", async () => {
    mockFetch.mockResolvedValueOnce(ok({ id: "m1", review_status: "corrected" }));
    await api.patchMapping("m1", "correct", "See appendix");
    const body = JSON.parse(mockFetch.mock.calls[0][1].body);
    expect(body.action).toBe("correct");
    expect(body.reviewer_note).toBe("See appendix");
  });
});

describe("api.getSummary", () => {
  it("fetches summary and returns it", async () => {
    const summary = {
      assessment_id: "a1",
      total_requirements: 3,
      satisfied: 1,
      weak: 1,
      ambiguous: 1,
      missing: 0,
      completion_pct: 33,
      requirements: [],
    };
    mockFetch.mockResolvedValueOnce(ok(summary));
    const result = await api.getSummary("a1");
    expect(result.completion_pct).toBe(33);
  });
});

describe("ApiError", () => {
  it("has correct status and message", () => {
    const e = new ApiError(422, "Validation error");
    expect(e.status).toBe(422);
    expect(e.message).toBe("Validation error");
    expect(e.name).toBe("ApiError");
  });

  it("is instanceof Error", () => {
    expect(new ApiError(500, "Server error")).toBeInstanceOf(Error);
  });
});
