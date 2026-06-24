package com.cisentinel.backend.scheduler;

import com.cisentinel.backend.model.PipelineRun;
import com.cisentinel.backend.service.AIAnalysisService;
import com.cisentinel.backend.service.GitHubService;
import com.cisentinel.backend.service.LogFetcherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PipelineMonitorScheduler {

    private final GitHubService gitHubService;
    private final AIAnalysisService aiAnalysisService;
    private final LogFetcherService logFetcherService;
    private final Set<String> analyzedRunIds = new HashSet<>();

    public PipelineMonitorScheduler(
        GitHubService gitHubService,
        AIAnalysisService aiAnalysisService,
        LogFetcherService logFetcherService
    ) {
        this.gitHubService = gitHubService;
        this.aiAnalysisService = aiAnalysisService;
        this.logFetcherService = logFetcherService;
    }

    @Scheduled(fixedDelay = 300000) // runs every 5 minutes
    public void monitorPipelines() {
        try {
            List<PipelineRun> runs = gitHubService.getPipelineRuns();

            for (PipelineRun run : runs) {
                // Only process failed runs we haven't seen before
                if ("failure".equals(run.getConclusion()) 
                        && !analyzedRunIds.contains(run.getId())) {
                    
                    System.out.println("New failure detected: " + run.getId() 
                        + " - " + run.getName());

                    String logs = logFetcherService.fetchLogs(run.getId());
                    String analysis = aiAnalysisService.analyzeLogs(
                        logs, run.getName(), run.getBranch(), run.getCommitMessage()
                    );

                    aiAnalysisService.postToSlack(
                        run.getName(), run.getBranch(),
                        run.getCommitMessage(), run.getConclusion(), analysis
                    );

                    analyzedRunIds.add(run.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Scheduler error: " + e.getMessage());
        }
    }
}