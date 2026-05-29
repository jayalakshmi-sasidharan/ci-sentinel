package com.cisentinel.backend.service;

import com.cisentinel.backend.model.PipelineRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final RestClient restClient;

    @Value("${github.api.owner}")
    private String owner;

    @Value("${github.api.repo}")
    private String repo;

    public GitHubService(RestClient.Builder restClientBuilder, 
                         @Value("${github.api.base-url}") String baseUrl,
                         @Value("${github.api.token}") String token) {
        
        // Configure the HTTP client with base URLs and Auth headers required by GitHub
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, token.isEmpty() ? "" : "Bearer " + token)
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<PipelineRun> getLiveWorkflowRuns() {
        // Target endpoint: /repos/{owner}/{repo}/actions/runs
        Map<String, Object> response = restClient.get()
                .uri("/repos/{owner}/{repo}/actions/runs?per_page=10", owner, repo)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("workflow_runs")) {
            return List.of();
        }

        List<Map<String, Object>> runs = (List<Map<String, Object>>) response.get("workflow_runs");

        // Map GitHub's native JSON structure directly into our SRE Dashboard model
        return runs.stream().map(run -> {
            Map<String, Object> headCommit = (Map<String, Object>) run.get("head_commit");
            Map<String, Object> actor = (Map<String, Object>) run.get("actor");
            
            // Calculate duration if timestamps are present
            String createdAt = (String) run.get("created_at");
            String updatedAt = (String) run.get("updated_at");
            long duration = 0; // Simplified placeholder for timestamp delta logic
            
            return PipelineRun.builder()
                    .id(String.valueOf(run.get("id")))
                    .name((String) run.get("name"))
                    .status((String) run.get("status"))
                    .conclusion((String) run.get("conclusion"))
                    .branch((String) run.get("head_branch"))
                    .commitMessage(headCommit != null ? (String) headCommit.get("message") : "No commit message")
                    .triggeredBy(actor != null ? (String) actor.get("login") : "unknown")
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .durationSeconds((int) duration)
                    .build();
        }).collect(Collectors.toList());
    }
}