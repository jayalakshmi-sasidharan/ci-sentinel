package com.cisentinel.backend.controller;

import com.cisentinel.backend.model.PipelineRun;
import com.cisentinel.backend.service.GitHubService;
import com.cisentinel.backend.service.AIAnalysisService;
import com.cisentinel.backend.service.LogFetcherService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {

    private final GitHubService gitHubService;
    private final AIAnalysisService aiAnalysisService;
    private final LogFetcherService logFetcherService;

    public PipelineController(GitHubService gitHubService, 
                            AIAnalysisService aiAnalysisService,
                            LogFetcherService logFetcherService) {
        this.gitHubService = gitHubService;
        this.aiAnalysisService = aiAnalysisService;
        this.logFetcherService = logFetcherService;
    }

    @GetMapping("/health")
    public String health() {
        return "CI Sentinel backend is running";
    }

    @GetMapping
    public List<PipelineRun> getPipelines() {
        return gitHubService.getPipelineRuns();
    }
    @GetMapping("/{id}/analyze")
    public String analyzePipeline(@PathVariable String id) {
        // Get the specific pipeline run
        List<PipelineRun> runs = gitHubService.getPipelineRuns();
        PipelineRun run = runs.stream()
            .filter(r -> r.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Pipeline run not found: " + id));

        // Fetch the logs
        String logs = logFetcherService.fetchLogs(id);

        // Analyze with Claude
        String analysis = aiAnalysisService.analyzeLogs(
            logs, run.getName(), run.getBranch(), run.getCommitMessage()
        );

        // Post to Slack
        aiAnalysisService.postToSlack(
            run.getName(), run.getBranch(), 
            run.getCommitMessage(), run.getConclusion(), analysis
        );

        return analysis;
    }
}