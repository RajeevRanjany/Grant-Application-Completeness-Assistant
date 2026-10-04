package com.grant.assistant.config;

import com.grant.assistant.service.ai.AIService;
import com.grant.assistant.service.ai.LLMProvider;
import com.grant.assistant.service.ai.MockProvider;
import com.grant.assistant.service.ai.OpenAICompatProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Value("${app.llm.provider:mock}")
    private String llmProvider;

    @Value("${app.llm.base-url:}")
    private String llmBaseUrl;

    @Value("${app.llm.api-key:}")
    private String llmApiKey;

    @Value("${app.llm.model:}")
    private String llmModel;

    @Bean
    public LLMProvider llmProvider() {
        if ("openai_compat".equals(llmProvider)) {
            return new OpenAICompatProvider(llmBaseUrl, llmApiKey, llmModel);
        }
        return new MockProvider();
    }

    @Bean
    public AIService aiService(LLMProvider llmProvider) {
        return new AIService(llmProvider, llmModel, 0.0);
    }
}
