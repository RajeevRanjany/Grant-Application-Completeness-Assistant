import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { SummaryPanel } from "../components/SummaryPanel.jsx";

const baseSummary = {
  assessment_id: "a1",
  total_requirements: 4,
  satisfied: 1,
  weak: 1,
  ambiguous: 1,
  missing: 1,
  completion_pct: 25,
  requirements: [],
};

describe("SummaryPanel", () => {
  it("renders the completion percentage", () => {
    render(<SummaryPanel summary={baseSummary} />);
    expect(screen.getByText("25%")).toBeInTheDocument();
  });

  it("renders all stat counts", () => {
    render(<SummaryPanel summary={baseSummary} />);
    expect(screen.getByText("4")).toBeInTheDocument();
    expect(screen.getAllByText("1")).toHaveLength(4);
  });

  it("renders stat labels", () => {
    render(<SummaryPanel summary={baseSummary} />);
    expect(screen.getByText("Satisfied")).toBeInTheDocument();
    expect(screen.getByText("Weak")).toBeInTheDocument();
    expect(screen.getByText("Ambiguous")).toBeInTheDocument();
    expect(screen.getByText("Missing")).toBeInTheDocument();
  });

  it("renders progress bar with correct width", () => {
    render(<SummaryPanel summary={baseSummary} />);
    const bar = screen.getByRole("progressbar");
    expect(bar).toHaveAttribute("aria-valuenow", "25");
    expect(bar).toHaveStyle({ width: "25%" });
  });

  it("renders 0% when nothing satisfied", () => {
    const summary = { ...baseSummary, satisfied: 0, completion_pct: 0 };
    render(<SummaryPanel summary={summary} />);
    expect(screen.getByText("0%")).toBeInTheDocument();
  });

  it("renders 100% when all satisfied", () => {
    const summary = {
      ...baseSummary,
      satisfied: 4,
      weak: 0,
      ambiguous: 0,
      missing: 0,
      completion_pct: 100,
    };
    render(<SummaryPanel summary={summary} />);
    expect(screen.getByText("100%")).toBeInTheDocument();
  });

  it("has a testid for selection", () => {
    render(<SummaryPanel summary={baseSummary} />);
    expect(screen.getByTestId("summary-panel")).toBeInTheDocument();
  });
});
