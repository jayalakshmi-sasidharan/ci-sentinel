package com.cisentinel.backend.controller;

import org.springframework.web.bind.annotation.*;

import com.cisentinel.backend.model.PipelineRun;

import java.util.List;

@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {
    
    @GetMapping("/health")
    public String health() {
        return "CI Sentinel backend is running";
    }

    @GetMapping
    public List<PipelineRun> getPipelines() {
        // This is a placeholder. In a real application, you would fetch this data from a database or an external API.
        return List.of(
            PipelineRun.builder()
                .id("1")
                .name("Build and Test")
                .status("completed")
                .conclusion("success")
                .branch("main")
                .commitMessage("Add health check endpoint")
                .triggeredBy("alice")
                .createdAt("2024-06-01T12:00:00Z")
                .updatedAt("2024-06-01T12:10:00Z")
                .durationSeconds(300)
                .build(),
            PipelineRun.builder()
                .id("2")
                .name("Deploy to Staging")
                .status("completed")
                .conclusion("failure")
                .branch("main")
                .commitMessage("Add new feature")
                .triggeredBy("alice")
                .createdAt("2024-06-01T12:15:00Z")
                .updatedAt("2024-06-01T12:25:00Z")
                .durationSeconds(600)
                .build()
        );
    }
}
