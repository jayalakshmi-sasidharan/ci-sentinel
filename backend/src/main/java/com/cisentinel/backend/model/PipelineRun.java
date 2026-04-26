package com.cisentinel.backend.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder

public class PipelineRun {
    private String id;
    private String name;
    private String status;
    private String conclusion;
    private String branch;
    private String commitMessage;
    private String triggeredBy;
    private String createdAt;
    private String updatedAt;
    private long durationSeconds;
}
