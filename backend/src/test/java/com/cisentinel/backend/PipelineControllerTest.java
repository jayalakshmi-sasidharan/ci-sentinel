package com.cisentinel.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PipelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndPointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/pipelines/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("CI Sentinel backend is running"));
    }

    @Test
    void getPipelinesReturnsList() throws Exception {
        mockMvc.perform(get("/api/pipelines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Build and Test"))
                .andExpect(jsonPath("$[0].status").value("completed"))
                .andExpect(jsonPath("$[0].conclusion").value("success"))
                .andExpect(jsonPath("$[0].branch").value("main"))
                .andExpect(jsonPath("$[0].commitMessage").value("Add health check endpoint"))
                .andExpect(jsonPath("$[0].triggeredBy").value("alice"))
                .andExpect(jsonPath("$[0].createdAt").value("2024-06-01T12:00:00Z"))
                .andExpect(jsonPath("$[0].updatedAt").value("2024-06-01T12:10:00Z"))
                .andExpect(jsonPath("$[0].durationSeconds").value(300));
    }

    @Test
    void getPipelinesReturnsMultipleEntries() throws Exception {
        mockMvc.perform(get("/api/pipelines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getPipelinesReturnsCorrectData() throws Exception {
        mockMvc.perform(get("/api/pipelines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Deploy to Staging"))
                .andExpect(jsonPath("$[1].status").value("completed"))
                .andExpect(jsonPath("$[1].conclusion").value("failure"))
                .andExpect(jsonPath("$[1].branch").value("main"))
                .andExpect(jsonPath("$[1].commitMessage").value("Add new feature"))
                .andExpect(jsonPath("$[1].triggeredBy").value("alice"))
                .andExpect(jsonPath("$[1].createdAt").value("2024-06-01T12:15:00Z"))
                .andExpect(jsonPath("$[1].updatedAt").value("2024-06-01T12:25:00Z"))
                .andExpect(jsonPath("$[1].durationSeconds").value(600));
    }
    
}
