package com.cisentinel.backend.controller;

import com.cisentinel.backend.model.PipelineRun;
import com.cisentinel.backend.service.GitHubService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {

    private final GitHubService gitHubService;

    public PipelineController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/health")
    public String health() {
        return "CI Sentinel backend is running";
    }

    @GetMapping
    public List<PipelineRun> getPipelines() {
        return gitHubService.getPipelineRuns();
    }
}