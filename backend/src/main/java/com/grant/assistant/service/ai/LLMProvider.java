package com.grant.assistant.service.ai;

import java.util.List;
import java.util.Map;

public interface LLMProvider {
    String complete(List<Map<String, String>> messages, String model, double temperature);
}
