package com.cisentinel.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.cisentinel.backend.service.GitHubService;
import com.cisentinel.backend.service.AIAnalysisService;
import com.cisentinel.backend.service.LogFetcherService;

@SpringBootTest
class BackendApplicationTests {

    @MockBean
    private GitHubService gitHubService;

    @MockBean
    private AIAnalysisService aiAnalysisService;

    @MockBean
    private LogFetcherService logFetcherService;

    @Test
    void contextLoads() {
    }
}