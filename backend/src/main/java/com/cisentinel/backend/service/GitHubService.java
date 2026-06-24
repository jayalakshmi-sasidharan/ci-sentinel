package com.cisentinel.backend.service;

import com.cisentinel.backend.model.PipelineRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubService {

    private final String token;
    private final String owner;
    private final String repo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GitHubService(
        @Value("${github.token}") String token,
        @Value("${github.owner}") String owner,
        @Value("${github.repo}") String repo
    ) {
        this.token = token;
        this.owner = owner;
        this.repo = repo;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<PipelineRun> getPipelineRuns() {
        String url = String.format(
            "https://api.github.com/repos/%s/%s/actions/runs", 
            owner, repo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode runs = root.get("workflow_runs");

            List<PipelineRun> result = new ArrayList<>();

            for (JsonNode run : runs) {
                long duration = 0;
                if (!run.get("created_at").isNull() && !run.get("updated_at").isNull()) {
                    // calculate duration in seconds between created and updated
                    String createdAt = run.get("created_at").asText();
                    String updatedAt = run.get("updated_at").asText();
                    duration = java.time.Duration.between(
                        java.time.Instant.parse(createdAt),
                        java.time.Instant.parse(updatedAt)
                    ).getSeconds();
                }

                result.add(PipelineRun.builder()
                    .id(run.get("id").asText())
                    .name(run.get("name").asText())
                    .status(run.get("status").asText())
                    .conclusion(run.get("conclusion").isNull() ? "in_progress" : run.get("conclusion").asText())
                    .branch(run.get("head_branch").asText())
                    .commitMessage(run.get("head_commit").get("message").asText())
                    .triggeredBy(run.get("triggering_actor").get("login").asText())
                    .createdAt(run.get("created_at").asText())
                    .updatedAt(run.get("updated_at").asText())
                    .durationSeconds(duration)
                    .build()
                );
            }
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch pipeline runs from GitHub: " + e.getMessage());
        }
    }
}