package com.grant.assistant.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class OpenAICompatProvider implements LLMProvider {

    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAICompatProvider(String baseUrl, String apiKey, String model) {
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = new ObjectMapper();

        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(60).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(60).toMillis());
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public String complete(List<Map<String, String>> messages, String model, double temperature) {
        if (baseUrl == null || baseUrl.isBlank() || apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException(
                    "OpenAI-compatible provider requires LLM_BASE_URL and LLM_API_KEY to be set");
        }

        String useModel = (model != null && !model.isBlank()) ? model : this.model;

        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", useModel);
            payload.put("temperature", temperature);

            ArrayNode messagesNode = objectMapper.createArrayNode();
            for (Map<String, String> msg : messages) {
                ObjectNode msgNode = objectMapper.createObjectNode();
                msgNode.put("role", msg.get("role"));
                msgNode.put("content", msg.get("content"));
                messagesNode.add(msgNode);
            }
            payload.set("messages", messagesNode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            JsonNode responseJson = objectMapper.readTree(response.getBody());
            return responseJson.get("choices").get(0).get("message").get("content").asText();

        } catch (org.springframework.web.client.HttpClientErrorException |
                 org.springframework.web.client.HttpServerErrorException e) {
            throw new RuntimeException("HTTP error from LLM provider: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error calling LLM provider: " + e.getMessage(), e);
        }
    }
}
