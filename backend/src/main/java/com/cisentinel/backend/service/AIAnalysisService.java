package com.cisentinel.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class AIAnalysisService {

    private final String anthropicApiKey;
    private final String slackWebhookUrl;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AIAnalysisService(
        @Value("${anthropic.api.key}") String anthropicApiKey,
        @Value("${slack.webhook.url}") String slackWebhookUrl
    ) {
        this.anthropicApiKey = anthropicApiKey;
        this.slackWebhookUrl = slackWebhookUrl;
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public String analyzeLogs(String logs, String pipelineName, String branch, String commitMessage) {
        try {
            String prompt = String.format("""
                You are a DevOps engineer analyzing CI/CD pipeline failure logs.
                
                Pipeline: %s
                Branch: %s
                Commit: %s
                
                Logs:
                %s
                
                Provide a concise 3-sentence analysis:
                1. What specifically failed
                2. The most likely root cause
                3. The recommended fix
                
                Be specific and actionable. No fluff.
                """, pipelineName, branch, commitMessage, 
                logs.length() > 3000 ? logs.substring(logs.length() - 3000) : logs);

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "claude-haiku-4-5-20251001");
            requestBody.put("max_tokens", 300);

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.set("messages", messages);

            String response = webClient.post()
                .uri("https://api.anthropic.com/v1/messages")
                .header("x-api-key", anthropicApiKey)
                .header("anthropic-version", "2023-06-01")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode responseJson = objectMapper.readTree(response);
            return responseJson.get("content").get(0).get("text").asText();

        } catch (Exception e) {
            return "Could not analyze logs: " + e.getMessage();
        }
    }

    public void postToSlack(String pipelineName, String branch, String commitMessage, 
                            String conclusion, String analysis) {
        try {
            String emoji = conclusion.equals("failure") ? "❌" : "✅";
            String message = String.format("""
                %s *Pipeline %s* on `%s`
                *Commit:* %s
                *AI Analysis:*
                %s
                """, emoji, conclusion.toUpperCase(), branch, commitMessage, analysis);

            ObjectNode slackBody = objectMapper.createObjectNode();
            slackBody.put("text", message);

            webClient.post()
                .uri(slackWebhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(slackBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        } catch (Exception e) {
            System.err.println("Failed to post to Slack: " + e.getMessage());
        }
    }
}