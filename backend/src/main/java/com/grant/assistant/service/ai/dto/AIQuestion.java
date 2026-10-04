package com.grant.assistant.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AIQuestion {

    @JsonProperty("requirement_index")
    private int requirementIndex;

    @JsonProperty("question_text")
    private String questionText;

    public AIQuestion() {}

    public int getRequirementIndex() { return requirementIndex; }
    public void setRequirementIndex(int v) { this.requirementIndex = v; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String v) { this.questionText = v; }
}
