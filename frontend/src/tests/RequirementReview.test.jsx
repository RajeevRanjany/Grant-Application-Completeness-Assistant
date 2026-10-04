import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { RequirementReview } from "../components/RequirementReview.jsx";

const req = {
  id: "r1",
  assessment_id: "a1",
  text: "Applicant must be a registered non-profit",
  classification: "mandatory",
  source_page: 1,
  source_excerpt: "Applicant must be a registered non-profit",
  order_index: 0,
};

const pendingMapping = {
  id: "m1",
  requirement_id: "r1",
  evidence_item_id: "e1",
  ai_confidence: "strong",
  ai_explanation: "Application directly states 501(c)(3) registration",
  guideline_citation: "must be a registered non-profit",
  ai_citation: "We are a registered 501(c)(3)",
  review_status: "pending",
  reviewer_note: null,
  reviewed_at: null,
};

const confirmedMapping = {
  ...pendingMapping,
  id: "m2",
  review_status: "confirmed",
  reviewed_at: "2026-10-03T10:00:00Z",
};

describe("RequirementReview", () => {
  it("renders requirement text", () => {
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={vi.fn()}
      />,
    );
    expect(
      screen.getByText("Applicant must be a registered non-profit"),
    ).toBeInTheDocument();
  });

  it("renders classification badge", () => {
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={vi.fn()}
      />,
    );
    expect(screen.getByText("mandatory")).toBeInTheDocument();
  });

  it("expands card on click and shows mapping details", async () => {
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={vi.fn()}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    expect(
      await screen.findByText("Application directly states 501(c)(3) registration"),
    ).toBeInTheDocument();
  });

  it("shows action buttons for pending mapping when expanded", async () => {
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={vi.fn()}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    expect(await screen.findByText("Confirm")).toBeInTheDocument();
    expect(screen.getByText("Correct")).toBeInTheDocument();
    expect(screen.getByText("Reject")).toBeInTheDocument();
  });

  it("hides action buttons for confirmed mapping", async () => {
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[confirmedMapping]}
        onReview={vi.fn()}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    await screen.findByTestId("mapping-row");
    expect(screen.queryByText("Confirm")).not.toBeInTheDocument();
  });

  it("calls onReview with confirm when Confirm is clicked", async () => {
    const onReview = vi.fn().mockResolvedValue(undefined);
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={onReview}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    fireEvent.click(await screen.findByText("Confirm"));
    await waitFor(() =>
      expect(onReview).toHaveBeenCalledWith("m1", "confirm", undefined),
    );
  });

  it("calls onReview with reject when Reject is clicked", async () => {
    const onReview = vi.fn().mockResolvedValue(undefined);
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={onReview}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    fireEvent.click(await screen.findByText("Reject"));
    await waitFor(() =>
      expect(onReview).toHaveBeenCalledWith("m1", "reject", undefined),
    );
  });

  it("shows correction textarea when Correct is clicked", async () => {
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={vi.fn()}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    fireEvent.click(await screen.findByText("Correct"));
    expect(screen.getByRole("textbox", { name: /correction note/i })).toBeInTheDocument();
  });

  it("calls onReview with note when correction is saved", async () => {
    const onReview = vi.fn().mockResolvedValue(undefined);
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={onReview}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    fireEvent.click(await screen.findByText("Correct"));

    const textarea = screen.getByRole("textbox", { name: /correction note/i });
    fireEvent.change(textarea, { target: { value: "Evidence is on page 4" } });
    fireEvent.click(screen.getByText("Save correction"));

    await waitFor(() =>
      expect(onReview).toHaveBeenCalledWith("m1", "correct", "Evidence is on page 4"),
    );
  });

  it("shows error message when onReview throws", async () => {
    const onReview = vi.fn().mockRejectedValue(new Error("Action failed"));
    render(
      <RequirementReview
        requirements={[req]}
        mappings={[pendingMapping]}
        onReview={onReview}
      />,
    );
    fireEvent.click(screen.getByRole("button", { expanded: false }));
    fireEvent.click(await screen.findByText("Confirm"));
    expect(await screen.findByText("Action failed")).toBeInTheDocument();
  });

  it("renders nothing when requirements are empty", () => {
    const { container } = render(
      <RequirementReview requirements={[]} mappings={[]} onReview={vi.fn()} />,
    );
    expect(container.firstChild).toBeNull();
  });
});
