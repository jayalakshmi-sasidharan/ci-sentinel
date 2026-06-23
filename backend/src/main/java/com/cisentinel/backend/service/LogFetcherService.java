package com.cisentinel.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class LogFetcherService {

    private final String token;
    private final String owner;
    private final String repo;
    private final RestTemplate restTemplate;

    public LogFetcherService(
        @Value("${github.token}") String token,
        @Value("${github.owner}") String owner,
        @Value("${github.repo}") String repo
    ) {
        this.token = token;
        this.owner = owner;
        this.repo = repo;
        this.restTemplate = new RestTemplate();
    }

    public String fetchLogs(String runId) {
        try {
            String url = String.format(
                "https://api.github.com/repos/%s/%s/actions/runs/%s/logs",
                owner, repo, runId
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Accept", "application/vnd.github+json");
            headers.set("X-GitHub-Api-Version", "2022-11-28");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );

            return response.getBody() != null ? response.getBody() : "No logs available";

        } catch (Exception e) {
            return "Failed to fetch logs: " + e.getMessage();
        }
    }
}